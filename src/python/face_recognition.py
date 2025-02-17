from deepface import DeepFace
import sys
import json
import numpy as np

def compare_faces(user_embedding, image2):
    try:
        # 🔹 Estrai l'embedding dal volto nel video
        result = DeepFace.represent(img_path=image2, model_name="Facenet")
        if len(result) == 0:
            return {"verified": False, "error": "No face detected"}

        # 🔹 Confronta il vettore numerico dell'utente con l'embedding estratto
        similarity = np.dot(user_embedding, result[0]["embedding"]) / (np.linalg.norm(user_embedding) * np.linalg.norm(result[0]["embedding"]))

        return {"verified": similarity > 0.75, "similarity": similarity}
    except Exception as e:
        return {"error": str(e)}

if __name__ == "__main__":
    user_embedding = np.array(eval(sys.argv[1]))  # Vettore dell'utente
    image2 = sys.argv[2]  # Immagine della miniatura
    print(json.dumps(compare_faces(user_embedding, image2)))
