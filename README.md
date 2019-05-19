# Chat-Application
It is is multi threaded chat application which is secure(encrypted) and let multiple Client to exchange texts asynchronously with each other through a Server.
It uses Vigenere Cipher for encryption and decryption.
All Clients connected to the server are provided with a client number.
Using this client number a client may message another client in the following format  ->  Client-No + ":"+ Text  
A Client may end its conversation with other clients by entering string "end".
There are two Java codes here, one for Client and the other for Server.
All the Clients must be connected to the same local Router/service provider with which the Server is connected for the message transmission to take place.
As both the Client and the Server sides are multi-threaded one can asynchronous send or receive text any any instant.
