# Scrapes logo images from carlogos.org
# Sources consulted: 
# - Beautiful Soup docs
# - https://stackoverflow.com/questions/41982475/scraper-in-python-gives-access-denied
# - https://stackoverflow.com/questions/18408307/how-to-extract-and-download-all-images-from-a-website-using-beautifulsoup
import bs4
from bs4 import BeautifulSoup
import requests
import re

def extract_source(url):
  headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0'}
  source = requests.get(url, headers=headers).text
  return source

def extract_img(url):
  headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0'}
  source = requests.get(url, headers=headers)
  return source

def download_images(site_url):
  response = extract_source(site_url)
  soup=bs4.BeautifulSoup(response)
  img_tags = soup.find_all('img')

  urls = [img['src'] for img in img_tags]

  for url in urls:
      filename = re.search(r'/([\w_-]+[.](jpg|png))$', url)
      if not filename:
          print("Regex didn't match with the url: {}".format(url))
          continue
      with open(filename.group(1), 'wb') as f:
          if 'http' not in url:
              url = '{}{}'.format(site_url, url)
          response = extract_img(url)
          print(response)
        #   f.write(response.content)

page_url = "https://www.carlogos.org/car-brands/"
download_images(page_url)
for i in range(2,8):
  download_images('{}page-{}.html'.format(page_url,i))