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
                    name = msg.data.name;
                    location = new Location()
                    {
                        lat = (double)msg.data.lat,
                        lon = (double)msg.data.lon
                    };

                    Send(new Message()
                    {
                        command = "register_successful",
                        data = null
                    });
                    break;
                case "sendInvitation":
                    var invitedPlayerID = msg.data.id;
                    var senderID = this.ID;
                    List<dynamic> data = new List<dynamic>();
                    var sender = msg.data.sender;
                    var invitationInfo = new Dictionary<string, object>();
                    var sendername = this.name;
                    invitationInfo.Add("sender", msg.data.sender);
                    invitationInfo.Add("sendername", sendername);
                    invitationInfo.Add("senderID", senderID);

                    data.Add(invitationInfo);
                    var match = false;
                    foreach (var item in Sessions.Sessions)
                    {
                        if (item.ID == invitedPlayerID)
                        {
                            match = true;
                            break;
                        }
                    }
                    if (match)
                    {
                        Sessions.SendTo(invitedPlayerID, Json.Encode(new Message()
                        {
                            command = "invite",
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

                    break;
                case "invitationAccept":
                    senderID = msg.data.id;
                    invitedPlayerID = this.ID;
                    List<dynamic> info = new List<dynamic>();
                    var acceptInfo = new Dictionary<string, object>();

                    acceptInfo.Add("senderID", senderID);
                    acceptInfo.Add("invitedPlayerID", invitedPlayerID);

                    foreach (var item in Sessions.Sessions)
                    {
                        var con = (NPuzzleConnection)item;
                        if ((con.ID == senderID) || (con.ID == invitedPlayerID))
                        {
                            con.inGame = true;
                        }
                    }

                    info.Add(acceptInfo);
                    Sessions.SendTo(invitedPlayerID, Json.Encode(new Message()
                        {
                            command = "inviteAccept",
                            data = info
                        }));
                    Sessions.SendTo(senderID, Json.Encode(new Message()
                        {
                            command = "inviteAccept",
                            data = info
                        }));
                    break;
                case "getPlayers":
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
                    break;
                default:
                    throw new Exception("Invalid command: " + msg.command);
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
