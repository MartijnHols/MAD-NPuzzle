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
        public string lat;
        public string lon;
    }

    public class NPuzzleConnection : WebSocketBehavior
    {
        private string name;
        private Location location;

        protected override void OnMessage(MessageEventArgs e)
        {
            var msg = getMessage(e);

            switch (msg.command)
            {
                case "register":
                    location = new Location() {
                        lat = msg.data.lat,
                        lon = msg.data.lon
                    };
                    break;
                case "getPlayers":
                    List<dynamic> players = new List<dynamic>();
                    foreach (var item in Sessions.Sessions)
                    {
                        var con = (NPuzzleConnection)item;
                        var playerInfo = new Dictionary<string, object>();
                        playerInfo.Add("id", con.ID);
                        playerInfo.Add("naam", con.name);
                        playerInfo.Add("location", con.location);
                        players.Add(playerInfo);
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
