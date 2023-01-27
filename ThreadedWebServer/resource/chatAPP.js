"using strict"
function handleMessageCB(event){
    console.log("WS sent a message");
    console.log(event.data);

    let messageParagraph = document.createElement("p"); // creates a paragraph element.
    let messageData = JSON.parse(event.data); // parses the message data

    let datestamp = Date.now() // sets varaible equal to the date and time.

// prints date
    let dateString = new Date(datestamp).toLocaleDateString() // takes the date and turns it to a string.
// prints time
    let timeString = new Date(datestamp).toLocaleTimeString() // takes the time and turns it to a string

    console.log(messageData);

    let username = messageData.user; // sets a variable equal to the user field
    let userMessage = messageData.message; // sets a variable equall to the message field
    let messageToSend = document.createTextNode(username + " " + dateString + " " + timeString + ": " + userMessage ); // creates a text node with all the final message combined.

    messageParagraph.appendChild(messageToSend); // adds the message to the paragraph
    messageRecieved.appendChild(messageParagraph); // adds the paragraph to the message received div.

    console.log(messageParagraph.value);

}

function handleConnectCB(){
    console.log("WS connected");
}

function handleCloseCB(){
    console.log("WS was closed");
}

function handleWSErrorCB(){
    console.log("WS had an error");
}

function handleButtonClickCB(event){
    console.log("in function");
    if (event.type == "click"){ // when the join button is clicked.
        userID = userName.value; // sets userID equal to whatever is in the username field.
        chatRoom = roomName.value; // sets chatRoom equal to whatever is in the roomName field.
        console.log("in loop");
        ws.send("join " + chatRoom); // sends join message.
        // visited = true;
    }

}

function handleMessageSendCB(event){
    if (event.type == "click"){ // if send message button is clicked.
        console.log("in loop 2");
        messageToSend = userMessage.value; // sets messageToSend to userMessage field value.
        // let test = userID + " " + messageToSend;
        // console.log(test);
        ws.send(userID + " " + messageToSend); // sends message in appropriate format.
        userMessage.value = ""; // clears userMessage field.
        console.log("message sent");
    }
}

// let visited = false
//html elements
let userName = document.getElementById("input_username");
let roomName = document.getElementById("input_roomName");
let userMessage = document.getElementById("userMessage");
// let messageArea = document.getElementById("message_area")
let messageRecieved = document.getElementById("message_box");

//html element values
let userID = null;
let chatRoom = null;
let messageToSend = null;
// let updatedMessage = messageRecieved.value;

//html buttons
let logInButton = document.getElementById("log_in_button");
let messageButton = document.getElementById("message_button");

//functions for when pressing join and message send buttons.
logInButton.addEventListener("click", handleButtonClickCB);
messageButton.addEventListener("click", handleMessageSendCB);

//declares new websocket
let ws = new WebSocket("ws://localhost:8080");

//declares websocket functions, most important of which is handle message.
ws.onmessage = handleMessageCB;
ws.onopen = handleConnectCB;
ws.onclose = handleCloseCB;
ws.onerror = handleWSErrorCB;
