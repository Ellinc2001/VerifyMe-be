package com.verifyMe.Utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

public class FaceEmbeddingUtil {

    private static final String PYTHON_PATH = "python";  // 🔹 Cambia con il tuo path Python se necessario
    private static final String SCRIPT_PATH = "src/python/face_recognition.py"; // 🔹 Percorso script Python DeepFace

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carica OpenCV
    }

    private static final CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_default.xml");

    // 🔹 Metodo per estrarre gli embedding facciali da un'immagine
    public static List<Mat> extractFaces(BufferedImage image) {
        List<Mat> faceList = new ArrayList<>();
        try {
            Mat matImage = bufferedImageToMat(image);
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(matImage, faceDetections);

            for (Rect rect : faceDetections.toArray()) {
                faceList.add(new Mat(matImage, rect)); // Ritaglia il volto
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return faceList;
    }

    public static boolean compareFaceEmbeddings(byte[] userEmbedding, String videoFacePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    PYTHON_PATH, SCRIPT_PATH, Arrays.toString(userEmbedding), videoFacePath
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();

            JSONObject result = new JSONObject(output.toString());
            return result.has("verified") && result.getBoolean("verified");  // True se il volto è stato riconosciuto
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 Metodo per convertire BufferedImage in Mat (OpenCV)
    private static Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = bi.getRGB(x, y);
                byte blue = (byte) (rgb & 0xFF);
                byte green = (byte) ((rgb >> 8) & 0xFF);
                byte red = (byte) ((rgb >> 16) & 0xFF);
                mat.put(y, x, new byte[]{blue, green, red});
            }
        }
        return mat;
    }

    // 🔹 Metodo per convertire byte array in float array
    public static float[] byteArrayToFloatArray(byte[] bytes) {
        int length = bytes.length / 4;
        float[] floats = new float[length];
        for (int i = 0; i < length; i++) {
            int asInt = ((bytes[i * 4] & 0xFF) << 24) | ((bytes[i * 4 + 1] & 0xFF) << 16)
                    | ((bytes[i * 4 + 2] & 0xFF) << 8) | (bytes[i * 4 + 3] & 0xFF);
            floats[i] = Float.intBitsToFloat(asInt);
        }
        return floats;
    }

    // 🔹 Metodo per convertire float array in byte array
    public static byte[] floatArrayToByteArray(float[] floats) {
        byte[] bytes = new byte[floats.length * 4];
        for (int i = 0; i < floats.length; i++) {
            int asInt = Float.floatToIntBits(floats[i]);
            bytes[i * 4] = (byte) ((asInt >> 24) & 0xFF);
            bytes[i * 4 + 1] = (byte) ((asInt >> 16) & 0xFF);
            bytes[i * 4 + 2] = (byte) ((asInt >> 8) & 0xFF);
            bytes[i * 4 + 3] = (byte) (asInt & 0xFF);
        }
        return bytes;
    }
}
