'use strict'

let stompClient
let username

$(function () {
    for (let i = 0; i < allMessages.length; i++)
        createMessageLine(allMessages[i]);
})


function createMessageLine(message) {
    const flexBox = document.createElement('div')
    flexBox.classList.add('message-feed')

    const messageElement = document.createElement('div')
    messageElement.className = 'media-body'

    const messageText = document.createElement('div')
    messageText.className = 'mf-content'
    messageText.innerText = message["content"]

    const date = document.createElement('small')
    date.className = 'mf-date'

    const clock = document.createElement('i')
    clock.classList.add('fa', 'fa-clock-o')

    clock.innerText = message.time
    date.appendChild(clock)


    messageElement.appendChild(messageText)
    messageElement.appendChild(date)

    const avatarContainer = document.createElement('div')
    const avatar = document.createElement('img')
    avatar.className = 'img-avatar'
    var source


    if (message["userFrom"].id === userFrom.id) {
        flexBox.classList.add('right')
        avatarContainer.classList.add('pull-right')
        source = userFrom["image"] != null ? '/image/' +
            userFrom["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'
    } else {
        flexBox.classList.add('left')
        avatarContainer.classList.add('pull-left')
        source = userTo["image"] != null ? '/image/' +
            userTo["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'
    }
    avatar.src = source
    avatarContainer.appendChild(avatar)
    flexBox.appendChild(avatarContainer)
    flexBox.appendChild(messageElement)

    const chat = document.querySelector('#chat')
    const textarea = document.querySelector('#message-controls')
    chat.appendChild(flexBox)
    chat.appendChild(textarea)
    chat.scrollTop = chat.scrollHeight
}


const connect = (event) => {
    username = userFrom["username"];
    if (username) {
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
}

const onError = (error) => {
    console.log(error)
}

const sendMessage = (event) => {
    const messageInput = document.querySelector('#message')
    const messageContent = messageInput.value.trim();
    let d = new Date();
    let ye = new Intl.DateTimeFormat('ru', { year: 'numeric' }).format(d);
    let mo = new Intl.DateTimeFormat('ru', { month: '2-digit' }).format(d);
    let da = new Intl.DateTimeFormat('ru', { day: '2-digit' }).format(d);
    let time = new Intl.DateTimeFormat('ru',
        {hour: "numeric",
                minute:"numeric",
    }).format(d)

    console.log(`${da}/${mo}/${ye} ${time}`)

    if (messageContent && stompClient) {
        const chatMessage = {
            userFrom: userFrom,
            userTo: userTo,
            content: messageInput.value,
            time: `${da}/${mo}/${ye} ${time}`
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

document.addEventListener('DOMContentLoaded', connect, true)
const messageControls = document.querySelector('#message-controls')
messageControls.addEventListener('submit', sendMessage, true)