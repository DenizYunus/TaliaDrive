using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace CameraFeed
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class GalleryPage : ContentPage
    {
        public GalleryPage()
        {
            InitializeComponent();
            GetImages();
        }

        public async void GetImages()
        {
            HttpClient client = new HttpClient();

            MultipartFormDataContent content = new MultipartFormDataContent();

            var values = new Dictionary<string, string> { { "Name", Communicator.name } };
            var json = JsonConvert.SerializeObject(values, Formatting.Indented);
            var stringContent = new StringContent(json);
            content.Add(stringContent, "data");

            string url = "http://18.116.82.71:8080/getGallery";
            var response = await client.PostAsync(url, content);

            string jsonGotFromGallery = response.Content.ReadAsStringAsync().Result;

            Console.WriteLine(jsonGotFromGallery);

            Images images = JsonConvert.DeserializeObject<Images>(jsonGotFromGallery);

            int imageCount = 0;
            foreach (ImageDetails imageDetail in images.images)
            {
                try
                {
                    Image image = new Image();
                    image.Source = "http://18.116.82.71/" + imageDetail.FileName;
                    Grid.SetRow(image, (int)Math.Floor((decimal)imageCount / 3));
                    Grid.SetColumn(image, imageCount % 3);
                    GalleryGrid.Children.Add(image);
                    imageCount++;
                }
                catch { }
            }

            //Image img = new Image();
            //img.Source = ImageSource.FromUri(new Uri("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png"));
            //Image img1 = new Image();
            //img1.Source = ImageSource.FromUri(new Uri("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg"));

            //Grid.SetRow(img, 0);
            //Grid.SetColumn(img, 0);
            //Grid.SetRow(img1, 0);
            //Grid.SetColumn(img1, 1);
            //GalleryGrid.Children.Add(img);
            //GalleryGrid.Children.Add(img1);
        }
    }

    public class Images
    {
        [JsonProperty("Images")]
        public ImageDetails[] images;
    }

    public class ImageDetails
    {
        [JsonProperty("Username")]
        public string Username { get; set; }

        [JsonProperty("Filename")]
        public string FileName { get; set; }

        [JsonProperty("Type")]
        public string FileType { get; set; }
    }
}