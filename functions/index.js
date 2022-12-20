'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

/**
 * Triggers when a user gets a new message and sends a notification.
 *
 * Users save their device notification tokens to `/tokens/{userId}/token/{notificationToken}`.
 */
exports.sendPushNotification = functions.firestore
    .document('messages/{chatId}/messages/{messageId}')
    .onWrite(async (change, context) => {
        const data = change.after.data();
        console.log(data);
        
        const content = data.content;
        const senderId = data.senderId;
        const receiverId = data.receiverId;
        const messageId = data.uid;

        const db = admin.firestore();
        const senderRef = db.doc('users/'+ senderId);
        const senderDoc = await senderRef.get();
        if (!senderDoc.exists) {
            console.log('No such document sender!');
            throw Error('No such document sender!');
        } else {
             console.log('Document data:', senderDoc.data());
        }
        const senderInfo = senderDoc.data();
        const senderName = senderInfo.name;
        const senderPhoto = senderInfo.photoUrl;

        const receiverTokenRef = db.doc('tokens/' + String(receiverId));
        const receiverTokenDoc = await receiverTokenRef.get();
        if (!receiverTokenDoc.exists) {
            console.log('No such document receiver!');
            throw Error('No such document receiver!');
        } else {
             console.log('Document data:', receiverTokenDoc.data());
        }
        const receiverToken = receiverTokenDoc.data().token;
        console.log('receiver token = ', receiverToken);

        // Create a notification
    const payload = {
        notification: {
            title:senderName,
            body: content,
            icon: senderPhoto,
            sound: "default"
        },
    };

  //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToDevice(receiverToken, payload, options)
    .then(function (response) {
        console.log("Successfully sent message:", response);
      })
      .catch(function (error) {
       console.log("Error sending message:", error);
      });;
    });