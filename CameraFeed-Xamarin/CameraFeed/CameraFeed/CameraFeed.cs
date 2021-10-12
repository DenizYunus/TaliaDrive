using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks.Dataflow;
using Xamarin.Forms;

namespace CameraFeed
{
    public class CameraFeed : ContentPage
    {

        public static readonly BindableProperty CameraOptionsProperty = BindableProperty.Create(
                propertyName: "CameraOptions",
                returnType: typeof(CameraOptions),
                declaringType: typeof(CameraFeed),
                defaultValue: CameraOptions.Rear
            );

        public CameraOptions CameraOption
        {
            get { return (CameraOptions)GetValue(CameraOptionsProperty); }
            set { SetValue(CameraOptionsProperty, value); }
        }

        public object BitmapFactory { get; private set; }

        public async void ProcessPhoto(object image)
        {
            if (Communicator.takePhotoActivated == true)
            {
                TakePhoto((byte[])image);
            } else if (Communicator.recordVideoActivated)
            {
                TakeVideo((byte[])image);
            }
        }

        async void TakePhoto(byte[] image)
        {
            HttpClient client = new HttpClient();

            MultipartFormDataContent content = new MultipartFormDataContent();

            var values = new Dictionary<string, string> { { "Name", "Deniz"} };

            var json = JsonConvert.SerializeObject(values, Formatting.Indented);

            var stringContent = new StringContent(json);

            content.Add(stringContent, "data");

            ByteArrayContent baContent = new ByteArrayContent(image);
            content.Add(baContent, "myFile");

            string url = "http://" + Communicator.ipAdress + ":8080/uploadImage";

            Console.WriteLine(url);

            var response =
                await client.PostAsync(url, content);

            Communicator.takePhotoActivated = false;
        }

        async void TakeVideo(byte[] image)
        {
            HttpClient client = new HttpClient();

            MultipartFormDataContent content = new MultipartFormDataContent();

            var values = new Dictionary<string, string> { { "Name", "Deniz" }, { "Frame", Communicator.frame.ToString() }, { "VideoId", Communicator.videoId } };
            Communicator.frame += 1;

            var json = JsonConvert.SerializeObject(values, Formatting.Indented);

            var stringContent = new StringContent(json);

            content.Add(stringContent, "data");

            ByteArrayContent baContent = new ByteArrayContent(image);
            content.Add(baContent, "myFile");

            string url = "http://" + Communicator.ipAdress + ":8080/uploadVideo";

            Console.WriteLine(url);

            var response =
                await client.PostAsync(url, content);

            Communicator.takePhotoActivated = false;
        }
    }
}
