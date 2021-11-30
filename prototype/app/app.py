from flask import Flask, render_template


app = Flask(__name__)

@app.route('/')
def index():
    posts = "POST"
    return render_template('index.html', posts=posts)