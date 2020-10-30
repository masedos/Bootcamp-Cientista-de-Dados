# -*- coding: utf-8 -*-
"""
Spyder Editor

"""

#import tweepy
from tweepy import OAuthHandler
from tweepy import Stream
from tweepy.streaming import StreamListener
import socket
import json


# Set up das credenciais
consumer_key    = ''
consumer_secret = ''
access_token    = ''
access_secret   = ''


class TweetsListener(StreamListener):

  def __init__(self, csocket):  #construtor
      self.client_socket = csocket
      self.count=0
      self.limit=30

  def on_data(self, data):
      try:
          msg = json.loads( data ) 			#lê os twitters
          self.count+=1            			#incrementa o contador
          if self.count<=self.limit:     
	          print(msg['text'].encode('utf-8'))	#verifica a quantidade de twitters lidos
	          self.client_socket.send( msg['text'].encode('utf-8')) #envia a mensagem para o socket
          return True
      except BaseException as e:
          print("Error on_data: %s" % str(e))
      return True

  def on_error(self, status):
      print(status)
      return True

def sendData(c_socket):     				#define como os dados devem ser enviados
  auth = OAuthHandler(consumer_key, consumer_secret)	#autentica no site
  auth.set_access_token(access_token, access_secret)	#obtém o token

  twitter_stream = Stream(auth, TweetsListener(c_socket)) #define o tipo de conexão
  twitter_stream.filter(track=['Bolsonaro'])  #define o filtro a ser utilizado 

if __name__ == "__main__":
  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)         # Cria o objeto socket (protocolo IPv6, TCP)
  host = "127.0.0.1"     # Obtém o "nome/endereço" local da maquina (localhost)
  port = 9995                 # Identifica e define a porta a ser utilizada.
  s.bind((host, port))        # Bind para a porta

  print("Listening on port: %s" % str(port))

  s.listen(5)                 # Aguarda a conexão.
  c, addr = s.accept()        # Estabelece a conexão.

  print("Received request from: " + str(addr))

  sendData(c)
