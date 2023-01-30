# TwitchFlix

This is a streaming and VOD platform with full user account functionality like livestreams, videos per channel and even user photos. 

Made with NGINX to handle the Streaming (RTMP), VOD distribution and in general file server (Like user photos and video thumbnails). NGINX is controlled by an HTTPS REST backend built in Java using Jetty and Jersey to handle all requests. This backend uses MariaDB (accessed utilizing HikariCP) and MongoDB as the database for the project.

This server supports Google Sign In or normal login, stored in the database that uses hashing and salting to safely store and compare passwords along with auth keys to prevent sending passwords back and forward from the server.

The client is an Android app that connects to the REST backend to manage authentication and to get the URLs of the files that are necessary to run the application. OkHttp (https://square.github.io/okhttp/) is used to make HTTPS requests, RTMP-RTSP stream client (https://github.com/pedroSG94/rtmp-rtsp-stream-client-java) is used to stream live video from NGINX and ExoPlayer (https://github.com/google/ExoPlayer) is used to display the videos and livestreams.

