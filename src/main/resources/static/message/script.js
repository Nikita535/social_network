'use strict'

let stompClient
let username

$(function () {
    connect()
    for (let i = 0; i < allMessages.length; i++)
        createMessageLine(allMessages[i]);
})


function createMessageLine(message){

    const flexBox = document.createElement('div')
    flexBox.classList.add('message-feed')

    const messageElement = document.createElement('div')
    messageElement.className = 'media-body'

    const messageText =document.createElement('div')
    messageText.className='mf-content'
    messageText.innerText=message["content"]

    const date = document.createElement('small')
    date.className='mf-date'

    const clock = document.createElement('i')
    clock.classList.add('fa fa-clock-o')

    date.appendChild(clock)
    date.innerText=message["time"]

    messageElement.appendChild(messageText,date)

    const avatarContainer=document.createElement('div')
    const avatar =document.createElement('img')
    avatar.className='img-avatar'
    var source = userFrom["image"]!=null ? '/image/' +
        userFrom["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png';
    avatar.src=source


    if (message["userFrom"].id === userFrom.id) {
        flexBox.classList.add('media')
        avatarContainer.classList.add('pull-left')
    }
    else {
        flexBox.classList.add('right')
        avatarContainer.classList.add('pull-right')
    }
    flexBox.appendChild(avatarContainer,messageElement)

    const chat = document.querySelector('#chat')
    chat.appendChild(flexBox)
    chat.scrollTop = chat.scrollHeight
}


const connect = (event) => {
    username = userFrom["username"];
    console.log(username)
    if (username) {
        const socket = new SockJS('/chat-example')
        stompClient = Stomp.over(socket)
        stompClient.connect({}, onConnected, onError)
    }
    event.preventDefault()
}

const onConnected = () => {
    console.log("dada")
    if (userFrom["id"] < userTo["id"])
        stompClient.subscribe('/topic/' + userFrom["id"] + "/" + userTo["id"], onMessageReceived)

    else
        stompClient.subscribe('/topic/' + userTo["id"] + "/" + userFrom["id"], onMessageReceived)
    //stompClient.send("/app/chat.newUser",
    //    {},
    //    JSON.stringify({sender: username})
    //)
}

const onError = (error) => {
    const status = document.querySelector('#status')
    status.innerHTML = 'Could not find the connection you were looking for. Move along. Or, Refresh the page!'
    status.style.color = 'red'
}

const sendMessage = (event) => {
    const messageInput = document.querySelector('#message')
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            userFrom: null,
            userTo: null,
            content: messageInput.value,
            time: new Date()
        }
        if (userFrom["id"] < userTo["id"])
            stompClient.send("/app/chat.send/" + userFrom["id"] + "/" + userTo["id"], {}, JSON.stringify(chatMessage))
        else
            stompClient.send("/app/chat.send/" + userTo["id"] + "/" + userFrom["id"], {}, JSON.stringify(chatMessage))
        messageInput.value = ''
    }
    event.preventDefault();
}


const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);
    createMessageLine(message);
}



const messageControls = document.querySelector('#message-controls')
messageControls.addEventListener('submit', sendMessage, true)