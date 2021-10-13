using System;
using System.Collections.Generic;
using System.Text;

namespace CameraFeed
{
    public static class Communicator
    {
        public static string name = "Name";

        public static bool takePhotoActivated = false;

        public static bool recordVideoActivated = false;
        public static int frame = 0;
        public static string videoId = "";

        public static string ipAdress = "";
    }
}
