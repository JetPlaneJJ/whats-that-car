# Cleans files in image subdirectories that are not compatible with Tensorflow
# Sources consulted: 
# - https://stackoverflow.com/questions/68191448/unknown-image-file-format-one-of-jpeg-png-gif-bmp-required
# - https://stackoverflow.com/questions/6996603/how-can-i-delete-a-file-or-folder-in-python
from pathlib import Path
import imghdr
import os

data_dir = "../../data/Car_Brand_Logos"
image_extensions = [".png", ".jpg"]
img_type_accepted_by_tf = ["bmp", "gif", "jpeg", "png"]

for filepath in Path(data_dir).rglob("*"):
    if filepath.suffix.lower() in image_extensions:
        img_type = imghdr.what(filepath)
        if img_type is None:
            print(f"{filepath} is not an image, removed")
            os.remove(filepath)
        elif img_type not in img_type_accepted_by_tf:
            print(f"{filepath} is a {img_type}, not accepted by TensorFlow, removed")
            os.remove(filepath)