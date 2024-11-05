DATASET :
https://www.kaggle.com/datasets/kaustubhb999/tomatoleaf
https://www.kaggle.com/datasets/psuryawanshi/tomato-disease-detection




Product Identification in Retail Store Shelf Images

This project utilizes three deep learning models—MobileNetV2, CNN, and DenseNet121—to identify Tomato leaf diseases based on images. Below is a comprehensive guide on executing the code through Android Studio.

I. Model Training

Prerequisites:
1. Ensure all necessary libraries are installed (e.g., TensorFlow, Keras).
2. Update the dataset paths in the training script as required.

Steps:
1. Open Visual Studio Code (VS Code).
2. Navigate to the training file for the model you want to train:
   - 1.1 MobileNetV2: `mobilenetv2.py`
   - 1.2 CNN: `CNN.py`
   - 1.3 DenseNet121: `densenet121.py`
3. Run the training script:
   ```bash
   python <model_name_train.py>
   ```
4. After training completes, the model will be saved as `<model_name.keras>`.
5.After training completes will download an tensorflow model file from the code which will be used for mobile application.


II. Model Testing with Android studio 

Steps:
1. Ensure the trained model file is accessible.
2.We have trained our model based on densenet121 model due its high accurary.
2. Locate the testing script for the respective model:
     - 2.1 DenseNet121: `densenet121.py`

III  Working nature of the mobile application 
  1. Place the tensorflow model of trained model in android studio and add appropriate Ui to the application .
  2. Upload an image  or capture image of an tomato leaf.
  3. Click the "Predict" button to view the predicted class.

Additional Notes
1. Ensure your Python environment is properly set up with all necessary libraries.
2. Use a GPU for faster model training and inference.
3. Adjust model parameters as needed based on dataset specifics for better accuracy.

By following these numbered steps, you will be able to train, test, and evaluate your product identification models effectively using both Streamlit and FastAPI. If you encounter issues, check the console for error messages and verify that dataset paths are correct.
