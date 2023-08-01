# Scrapes logo images from carlogos.org car brands page
# Sources consulted:
# - Beautiful Soup docs
# - https://stackoverflow.com/questions/41982475/scraper-in-python-gives-access-denied
# - https://stackoverflow.com/questions/18408307/how-to-extract-and-download-all-images-from-a-website-using-beautifulsoup
import bs4
from bs4 import BeautifulSoup
import requests
import re

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0'}
home_site = "https://www.carlogos.org"
page_url = f'{home_site}/car-brands'


def download_images(site_url):
    source = requests.get(site_url, headers=headers).text
    soup = bs4.BeautifulSoup(source, features='lxml')
    img_tags = soup.find_all('img')

    urls = [img['src'] for img in img_tags]
    for url in urls:
        img_url = f'{home_site}/{url}'
        print(img_url)
        filename = re.search(r'/([\w_-]+[.](jpg|png))$', url)
        if not filename:
            print("Regex didn't match with the url: {}".format(url))
            continue
        with open(f'images/{filename.group(1)}', 'wb') as f:
            if 'http' not in url:
                url = '{}{}'.format(site_url, url)
            response = requests.get(img_url, headers=headers)
            f.write(response.content)
            f.close()


download_images(page_url)

# Download from other pages
# for i in range(2,8):
#   download_images('{}page-{}.html'.format(page_url,i))
