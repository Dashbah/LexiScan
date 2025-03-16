FROM python:3.8

WORKDIR /practice-security/kv
COPY requirements.txt .

RUN pip install -r requirements.txt

COPY * .

ENTRYPOINT ["python", "server.py"]