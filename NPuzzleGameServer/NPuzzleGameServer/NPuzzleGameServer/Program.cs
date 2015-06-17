using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Helpers;
using WebSocketSharp;
using WebSocketSharp.Server;

namespace NPuzzleGameServer
{
    public class NPuzzleGameServer
    {

    }

    public struct Message
    {
        public string command;
        public dynamic data;
    }
    public struct Location
    {
        public double lat;
        public double lon;
    }

    public class NPuzzleConnection : WebSocketBehavior
    {
        private string name;
        private Location location;
        private bool inGame = false;

        protected override void OnMessage(MessageEventArgs e)
        {
            var msg = getMessage(e);

            switch (msg.command)
            {
                case "register":
                    // De speler meldt zichzelf aan op de opgegeven locatie
                    register(msg.data.name, (double)msg.data.lat, (double)msg.data.lon);
                    break;
                case "sendInvite":
                    // De speler stuurt een invite naar het opgegeven id
                    sendInvite(msg.data.playerId);
                    break;
                case "invitationAccept":
                    // De speler accepteerd een ontvangen invite
                    acceptInvite(msg.data.id);
                    break;
                case "getPlayers":
                    // De speler vraagt een lijst met spelers op
                    getPlayers();
                    break;
                case "startGame":
                    // De speler heeft een afbeelding en moeilijkheisgraad gekozen en de game moet starten
                    startGame(msg.data.invitedPlayerId, msg.data.resourceId, msg.data.difficulty);
                    break;
                case "sendEffect":
                    // De speler heeft een rij afgerond en stuurt een negatief effect naar de tegenstander
                    sendEffect(msg.data.versusPlayerId);
                    break;
                case "gameComplete":
                    gameComplete(msg.data.versusPlayerId);
                    break;
                default:
                    throw new Exception("Invalid command: " + msg.command);
            }
        }

        private void gameComplete(string versusPlayerId)
        {
            Sessions.SendTo(versusPlayerId, Json.Encode(new Message()
                {
                    command = "gameLost",
                    data = null
                }));
        }

        private void sendEffect(string versusPlayerId)
        {
            Sessions.SendTo(versusPlayerId, Json.Encode(new Message()
                {
                    command = "effectRecieved",
                    data = null
                }));
        }

        private void register(string name, double lat, double lng)
        {
            this.name = name;
            location = new Location()
            {
                lat = lat,
                lon = lng
            };

            Send(new Message()
            {
                command = "register_successful",
                data = null
            });
        }

        private void sendInvite(string toPlayerId)
        {
            var data = new Dictionary<string, object>();
            var sendername = this.name;
            data.Add("sendername", sendername);
            data.Add("senderID", this.ID);

            var match = false;
            foreach (var item in Sessions.Sessions)
            {
                if (item.ID == toPlayerId)
                {
                    match = true;
                    break;
                }
            }
            if (match)
            {
                Sessions.SendTo(toPlayerId, Json.Encode(new Message()
                {
                    command = "inviteReceived",
                    data = data
                }));
            }
            else
            {
                Send(new Message()
                {
                    command = "playerUnavailable",
                    data = data
                });
            }
        }

        private void getPlayers()
        {
            List<dynamic> players = new List<dynamic>();
            foreach (var item in Sessions.Sessions)
            {
                var con = (NPuzzleConnection)item;
                if (con.name != null && !con.inGame)
                {
                    var playerInfo = new Dictionary<string, object>();
                    playerInfo.Add("id", con.ID);
                    playerInfo.Add("naam", con.name);
                    playerInfo.Add("location", con.location);
                    players.Add(playerInfo);
                }
            }

            Send(new Message()
            {
                command = "players",
                data = players
            });
        }

        private void acceptInvite(string inviterId)
        {
            var data = new Dictionary<string, object>();
            data["senderId"] = inviterId;
            data["invitedPlayerId"] = this.ID;

            Sessions.SendTo(inviterId, Json.Encode(new Message()
            {
                command = "inviteAccepted",
                data = data
            }));
            Sessions.SendTo(this.ID, Json.Encode(new Message()
            {
                command = "inviteAcceptedSuccessful",
                data = data
            }));

            foreach (var item in Sessions.Sessions)
            {
                var con = (NPuzzleConnection)item;
                if ((con.ID == inviterId) || (con.ID == this.ID))
                {
                    con.inGame = true;
                }
            }
        }

        public void startGame(string invitedPlayerId, long resourceId, int difficulty)
        {
            {
                var data = new Dictionary<string, object>();
                data["otherPlayerId"] = this.ID;
                data["resourceId"] = resourceId;
                data["difficulty"] = difficulty;

                Sessions.SendTo(invitedPlayerId, Json.Encode(new Message()
                {
                    command = "startGame",
                    data = data
                }));
            }
            {
                var data = new Dictionary<string, object>();
                data["otherPlayerId"] = invitedPlayerId;
                data["resourceId"] = resourceId;
                data["difficulty"] = difficulty;

                Sessions.SendTo(this.ID, Json.Encode(new Message()
                {
                    command = "startGame",
                    data = data
                }));
            }
        }

        private void Send(Message msg)
        {
            Send(Json.Encode(msg));
        }

        private static Message getMessage(MessageEventArgs e)
        {
            dynamic json = Json.Decode(e.Data);

            return new Message()
            {
                command = json.command,
                data = json.data
            };
        }

        protected override void OnOpen()
        {
            base.OnOpen();
        }

        protected override void OnError(WebSocketSharp.ErrorEventArgs e)
        {
            base.OnError(e);
        }

        protected override void OnClose(CloseEventArgs e)
        {
            base.OnClose(e);
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            var wssv = new WebSocketServer(1337);
            wssv.AddWebSocketService<NPuzzleConnection>("/");
            wssv.Start();
            Console.ReadKey(true);
            wssv.Stop();
        }

    }
}
