from flask import Flask, jsonify
import subprocess

app = Flask(__name__)

@app.route('/health')
def health_check():
    result = subprocess.run(['ps', 'aux'], stdout=subprocess.PIPE)
    if b'java -jar /app/application.jar' in result.stdout:
        return jsonify(status='Healthy'), 200
    else:
        return jsonify(status='Unhealthy'), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)