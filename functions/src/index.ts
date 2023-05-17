import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const firestore = admin.firestore();

export const updatePostId = functions
  .region("europe-west1")
  .firestore.document("posts/{postId}")
  .onCreate(async (snapshot, context) => {
    const postId = context.params.postId;
    const postRef = snapshot.ref;

    await postRef.update({postId: postId});
  });

export const updateUserUId = functions
  .region("europe-west1")
  .firestore.document("users/{userUID}")
  .onCreate(async (snapshot, context) => {
    const userUID = context.params.userUID;
    const userRef = snapshot.ref;

    await userRef.update({userUID: userUID});
  });

export const deleteDeclinedFriendship = functions
  .region("europe-west1")
  .firestore.document("friendships/{friendshipId}")
  .onUpdate((change, context) => {
    const newValue = change.after.data();

    if (newValue.status === "declined") {
      return firestore.collection("friendships").doc(context.params.friendshipId).delete();
    } else {
      return null;
    }
  });

export const createFriendShipIdAddsendNotification = functions
  .region("europe-west1")
  .firestore.document("friendships/{friendshipId}")
  .onCreate(async (snapshot, context) => {
    const friendshipId = context.params.friendshipId;
    const friendshipData = snapshot.data();

    const accepterUid = friendshipData.accepter.userUID;
    const requester = friendshipData.requester;

    await snapshot.ref.update({friendShipId: friendshipId});
    console.log(`Updated friendship document with id ${friendshipId}`);

    const accepterRef = firestore.collection("users").doc(accepterUid);

    const accepterData = await accepterRef.get();

    const tokens: string[] = [];

    if (accepterData.exists) {
      const accepterUser = accepterData.data();
      if (accepterUser != undefined) {
        tokens.push(...accepterUser.fcmToken);
      }
    } else {
      console.log("There is no accepterData");
      return null;
    }

    console.log(`Found ${tokens.length} FCM tokens for user ${accepterUid}`);
    const placeholder = "PLACEHOLDER_FOR_USER_IMAGE";
    const notification: admin.messaging.NotificationMessagePayload = {
      title: "OurTrips",
      body: `${requester.username} added you as a friend`,
      imageUrl: requester.profilePictureUrl ? requester.profilePictureUrl : placeholder,
    };

    const message: admin.messaging.MulticastMessage = {
      notification,
      tokens,
    };

    console.log(`Sending multicast message to ${tokens.length} FCM tokens`);

    const response = await admin.messaging().sendEachForMulticast(message);

    console.log(`Sent notifications to ${response.successCount} devices`);
    return response;
  });

export const acceptOrDeclineFriendShipNotification = functions
  .region("europe-west1")
  .firestore.document("friendships/{friendshipId}")
  .onUpdate(async (change, context) => {
    const newFriendshipData = change.after.data();
    const accepter = newFriendshipData.accepter;
    if (newFriendshipData.status === "declined") {
      return firestore.collection("friendships").doc(context.params.friendshipId).delete();
    } else if (newFriendshipData.status === "accepted") {
      const requesterUid = newFriendshipData.requester.userUID;
      const requesterRef = firestore.collection("users").doc(requesterUid);
      const requesterData = await requesterRef.get();
      const tokens: string[] = [];
      if (requesterData.exists) {
        const requesterUser = requesterData.data();
        if (requesterUser != undefined) {
          tokens.push(...requesterUser.fcmToken);
        }
      } else {
        console.log("There is no requesterData");
      }
      console.log(`Found ${tokens.length} FCM tokens for user ${requesterUid}`);
      const placeholder = "PLACEHOLDER_FOR_USER_IMAGE";
      const notification: admin.messaging.NotificationMessagePayload = {
        title: "OurTrips",
        body: `${accepter.username} accepted your friend request`,
        imageUrl: accepter.profilePictureUrl ? accepter.profilePictureUrl : placeholder,
      };
      const message: admin.messaging.MulticastMessage = {
        notification,
        tokens,
      };
      console.log(`Sending multicast message to ${tokens.length} FCM tokens`);
      const response = await admin.messaging().sendEachForMulticast(message);
      console.log(`Sent notifications to ${response.successCount} devices`);
      return response;
    } else {
      return null;
    }
  });

/**
 * Adds two numbers together and returns the result.
 * @param {string} userUID -
 *  An array of user UIDs for whom to retrieve FCM tokens.
 * @return{Promise<string[]>}
 * A promise that resolves with an array of FCM tokens for the specified users.
 */
async function getFriendList(userUID: string): Promise<string[]> {
  console.log(`Getting friend list for user ${userUID}`);

  const colRef = admin.firestore().collection("friendships");
  const friendList: string[] = [];

  console.log(`Querying accepted friendships where ${userUID} is the accepter`);
  const posterAccepterList = await colRef
    .where("status", "==", "accepted")
    .where("accepter.userUID", "==", userUID)
    .get();
  posterAccepterList.docs.map((doc) => {
    const data = doc.data();
    friendList.push(data.requester.userUID);
  });

  console.log(`Querying accepted friendships where ${userUID} is the requester`);
  const posterRequesterList = await colRef
    .where("status", "==", "accepted")
    .where("requester.userUID", "==", userUID)
    .get();
  posterRequesterList.docs.map((doc) => {
    const data = doc.data();
    friendList.push(data.accepter.userUID);
  });

  console.log(`Friend list for user ${userUID}: ${friendList}`);
  return friendList;
}

/**
 * a
 * @param {string[]}userUIDs
 * @return{Promise<string[]>}
 */
async function getFriendTokens(userUIDs: string[]): Promise<string[]> {
  const friendTokens: string[] = [];
  const colRef = admin.firestore().collection("users");
  const userDocs = await colRef.where("userUID", "in", userUIDs).get();

  userDocs.forEach((doc) => {
    const user = doc.data();
    console.log("User data: ", user);
    if (Array.isArray(user.fcmToken) && user.fcmToken.length > 0) {
      console.log("FCM tokens found for user: ", user.userUID);
      friendTokens.push(...user.fcmToken);
    }
  });
  console.log("Friend tokens: ", friendTokens);
  return friendTokens;
}
export const insertPostIdAndSendNotification = functions
  .region("europe-west1")
  .firestore.document("posts/{postId}")
  .onCreate(async (snapshot, context) => {
    const postId = context.params.postId;
    const postRef = snapshot.ref;
    const postData = snapshot.data();
    await postRef.update({postId: postId});
    const friendList = await getFriendList(postData.user.userUID);
    const tokens = await getFriendTokens(friendList);
    if (tokens.length === 0) {
      console.log("No friend tokens found");
      return;
    }

    const notification: admin.messaging.NotificationMessagePayload = {
      title: `${postData.user.username} posted a new photo!`,
      body: "Check it out!",
      imageUrl: postData.photo_url,
    };

    const message: admin.messaging.MulticastMessage = {
      notification,
      tokens,
    };
    console.log(`Sending multicast message to ${tokens.length} FCM tokens`);
    const response = await admin.messaging().sendEachForMulticast(message);
    console.log(`Sent notifications to ${response.successCount} devices`);
    return response;
  });
