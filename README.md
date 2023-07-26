# Whats That Vehicle
Project Summary: Mobile app that allows a user to take a picture of a car and use a ML model to classify the make of the car (further details like model, model year...etc TBD). Users can also look up a make/model by VIN and other details.

## Usage 
- Unzip Car_Brand_Logos.zip for the preprocessing scripts

## Project Contents
/data/ - images used for training
|-...
/models/ - saved models
|- ...
/preprocessing_scripts/
|-image_cleaner.py - removes corrupt or incompatible files from dataset
|-image_to_subdirectory - puts images into their class subdirectory (ex: hyundai photo in hyundai folder)
|-logo_scraper.py - WIP scrapes car logos from websites

# Tools and sources used
## Kaggle Notebook: https://www.kaggle.com/code/jiojlggqgrqgy5/car-brand-classification/edit
- Used for training the model
- WIP for improvements

## Dataset
- https://www.kaggle.com/datasets/volkandl/car-brand-logos uploaded to Kaggle by user 'Volkan Özdemir'