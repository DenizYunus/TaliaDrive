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
                ///TODO HERE WILL BE PHOTO TAKEN
                var stream = new MemoryStream((byte[])image);

                

                HttpClient client = new HttpClient();

                MultipartFormDataContent content = new MultipartFormDataContent();

                ByteArrayContent baContent = new ByteArrayContent((byte[])image);
                content.Add(baContent, "myFile");

                //StringContent emailText = new StringContent(lbl_email.Text);
                //content.Add(emailText, "email");

                string url = "http://" + Communicator.ipAdress + ":8080/uploadImage";

                Console.WriteLine(url);

                var response =
                    await client.PostAsync(url, content);

                //ToasText = response.Content.ReadAsStringAsync().Result;


                Communicator.takePhotoActivated = false;
            }
        }
    }
}
