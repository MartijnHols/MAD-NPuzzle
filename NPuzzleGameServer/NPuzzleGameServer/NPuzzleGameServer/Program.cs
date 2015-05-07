using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WebSocketSharp;
using WebSocketSharp.Server;

namespace NPuzzleGameServer
{
    public class NPuzzleGameServer: WebSocketBehavior
    {
        protected override void OnMessage(MessageEventArgs e)
        {
            Console.WriteLine(e.Data);
        }

        protected override void OnOpen()
        {
            base.OnOpen();
        }

        protected override void OnError(ErrorEventArgs e)
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
            wssv.AddWebSocketService<NPuzzleGameServer>("/");
            wssv.Start();
            Console.ReadKey(true);
            wssv.Stop();
        }

    }
}
