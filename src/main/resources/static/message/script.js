'use strict'

let stompClient
let username

$(function () {
    for (let i = 0; i < allMessages.length; i++)
        createMessageLine(allMessages[i]);
})


function createMessageLine(message){
    const chatCard = document.createElement('div')
    chatCard.className = 'card-body'

    const flexBox = document.createElement('div')



    const messageElement = document.createElement('div')
    messageElement.className = 'msg_container_send'


    messageElement.classList.add('chat-message')

    const avatarContainer = document.createElement('div')
    avatarContainer.className = 'img_cont_msg'
    const avatarElement = document.createElement('div')
    avatarElement.className = 'circle user_img_msg'
    const avatarText = document.createTextNode(message.userFrom["username"][0])
    avatarElement.appendChild(avatarText);
    avatarElement.style['background-color'] = getAvatarColor(message.userFrom["username"][0])
    avatarContainer.appendChild(avatarElement)

    messageElement.style['background-color'] = getAvatarColor(message.userFrom["username"][0])

    flexBox.appendChild(avatarContainer)

    if (message["userFrom"].id === userFrom.id) {
        flexBox.className = 'd-flex justify-content-end mb-4'
        messageElement.classList.add("mr-2")
        flexBox.appendChild(messageElement)
        flexBox.appendChild(avatarContainer)
    }
    else {
        flexBox.className = 'd-flex justify-content-start mb-4'
        messageElement.classList.add("ml-2");
        flexBox.appendChild(avatarContainer)
        flexBox.appendChild(messageElement)
    }

    chatCard.appendChild(flexBox)


    messageElement.innerHTML = message.content

    const chat = document.querySelector('#chat')
    chat.appendChild(flexBox)
    chat.scrollTop = chat.scrollHeight
}


const connect = (event) => {
    username = userFrom["username"];

    if (username) {
        const login = document.querySelector('#login')
        login.classList.add('hide')

        const chatPage = document.querySelector('#chat-page')
        chatPage.classList.remove('hide')

        const socket = new SockJS('/chat-example')
        stompClient = Stomp.over(socket)
        stompClient.connect({}, onConnected, onError)
    }
    event.preventDefault()
}

const onConnected = () => {
    if (userFrom["id"] < userTo["id"])
        stompClient.subscribe('/topic/' + userFrom["id"] + "/" + userTo["id"], onMessageReceived)
    else
        stompClient.subscribe('/topic/' + userTo["id"] + "/" + userFrom["id"], onMessageReceived)
    //stompClient.send("/app/chat.newUser",
    //    {},
    //    JSON.stringify({sender: username})
    //)
    const status = document.querySelector('#status')
    status.className = 'hide'
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
            userFrom: userFrom,
            userTo: userTo,
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

const hashCode = (str) => {
    let hash = 0
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash)
    }
    return hash
}


const getAvatarColor = (messageSender) => {
    const colours = ['#2196F3', '#32c787', '#1BC6B4', '#A1B4C4']
    const index = Math.abs(hashCode(messageSender) % colours.length)
    return colours[index]
}

const loginForm = document.querySelector('#login-form')
loginForm.addEventListener('submit', connect, true)
const messageControls = document.querySelector('#message-controls')
messageControls.addEventListener('submit', sendMessage, true)