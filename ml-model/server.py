from flask import Flask, request, jsonify
import onnxruntime as ort
import numpy as np
from PIL import Image
import io

app = Flask(__name__)

# Загрузка ONNX модели
model_path = "best-model.onnx"
session = ort.InferenceSession(model_path)

def preprocess_image(image_bytes):
    # Преобразование изображения в формат, подходящий для модели
    image = Image.open(io.BytesIO(image_bytes))
    image = image.resize((224, 224))  # Пример изменения размера, зависит от модели
    image = np.array(image).astype(np.float32)
    image = np.transpose(image, (2, 0, 1))  # Изменение порядка каналов, если необходимо
    image = np.expand_dims(image, axis=0)  # Добавление batch dimension
    return image

@app.route('/ml-model/count', methods=['POST'])
def count():
    data = request.json
    rquid = data['rquid']
    image_bytes = data['image']

    # Преобразование изображения
    input_image = preprocess_image(image_bytes)

    # Выполнение модели
    input_name = session.get_inputs()[0].name
    output_name = session.get_outputs()[0].name
    result = session.run([output_name], {input_name: input_image})

    # Получение результата
    percentage = float(result[0][0])  # Пример, зависит от модели

    # Возвращение результата
    return jsonify({
        'rquid': rquid,
        'percentage': percentage
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)