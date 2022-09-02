'use strict'

let stompClient
let username
let currentLocation = document.location.protocol + "//" + document.location.host
const chat = document.querySelector('#chat')

function checkInChat() {
    jQuery.ajax({
        type: 'POST',
        url: currentLocation + "/isUserInChat/" + userTo["id"],
        contentType: 'application/json',
        success: sendPushNotification
    });
}


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

    clock.innerText = message.time.substring(0, message.time.length - 3)
    date.appendChild(clock)


    let images_post = document.createElement('div')
    images_post.classList.add("timeline-footer")
    let images = message.images
    if (images != null) {
        for (let j = 0; j < images.length; j++) {
            let imgcontainer = document.createElement('div')
            imgcontainer.classList.add("card-body")
            let postsource = images[j] != null ? '/image/' +
                images[j].id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
            imgcontainer.innerHTML = "<img src=\"" + postsource + "\" alt=\"\">"
            images_post.innerHTML += imgcontainer.outerHTML
        }

        messageText.appendChild(images_post)
    }


    messageElement.appendChild(messageText)
    messageElement.appendChild(date)

    const avatarContainer = document.createElement('div')
    const avatar = document.createElement('img')
    avatar.className = 'img-avatar'
    let source

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

    return flexBox

}

function fetchMessages(message) {
    chat.insertBefore(createMessageLine(message), chat.firstChild)
}

function sendCreatedMessage(message) {

    chat.appendChild(createMessageLine(message))
    chat.scrollTop = chat.scrollHeight
}


function fetchLastMessage(message) {
    let friendListItem
    let lastMessageText = message.content.length < 7 ? message.content : message.content.substring(0, 7) + '...'
    let lastMessageUser

    if (message.userFrom.id === userFrom.id) {
        lastMessageUser = 'Вы: '
        friendListItem = document.getElementById(message.userTo.username)
    } else {
        lastMessageUser = message.userTo.profileInfo.name + ": "

        friendListItem = document.getElementById(message.userFrom.username)
    }

    friendListItem.querySelectorAll('.list-group-item-text')[0].innerHTML = lastMessageUser + lastMessageText
    friendListItem.querySelectorAll('.list-group-item-text')[1].innerHTML = message.time.split(" ")[1].substring(0, 5)
    let friendListItemContainer = friendListItem.parentNode
    let friendList = friendListItemContainer.parentNode
    friendList.removeChild(friendListItemContainer)
    friendList.insertBefore(friendListItemContainer, friendList.firstChild)

}


const connect = () => {
    username = userFrom["username"];
    if (username) {
        const socket = new SockJS('/chat-example')
        stompClient = Stomp.over(socket)
        stompClient.connect({}, onConnected, onError)
    }
}


const onConnected = () => {
    if (userFrom["id"] < userTo["id"])
        stompClient.subscribe('/topic/' + userFrom["id"] + "/" + userTo["id"], onMessageReceived,
            {id: '/topic/' + userFrom["id"] + "/" + userTo["id"]})

    else
        stompClient.subscribe('/topic/' + userTo["id"] + "/" + userFrom["id"], onMessageReceived,
            {id: '/topic/' + userTo["id"] + "/" + userFrom["id"]})
}

const onError = (error) => {
    console.log(error)
}

function constructMessageObject(messageInput) {
    let d = new Date();
    let ye = new Intl.DateTimeFormat('ru', {year: 'numeric'}).format(d);
    let mo = new Intl.DateTimeFormat('ru', {month: '2-digit'}).format(d);
    let da = new Intl.DateTimeFormat('ru', {day: '2-digit'}).format(d);
    let time = new Intl.DateTimeFormat('ru',
        {
            hour: "numeric",
            minute: "numeric",
            second: "numeric"
        }).format(d)

    return {
        userFrom: userFrom,
        userTo: userTo,
        content: messageInput.value,
        time: `${da}/${mo}/${ye} ${time}`
    }
}

async function createMessageImage(images) {
    var request = {};
    let elem;
    request.images = images; // some data

    let formData = new FormData();
    for (let i = 0; i < images.length; i++) {
        formData.append("file", images[i]);
    }
    const response = await fetch(currentLocation + "/message/create", {
        method: "POST",
        body: formData,
    }).then((data) => {
        elem = data.json();
    })
    return elem
}


function sendPushNotification(data) {
    let isConnected = data
    const messageInput = document.querySelector('#message')
    const messageContent = messageInput.value.trim();

    const chatMessage = constructMessageObject(messageInput)
    if (messageContent && stompClient && !isConnected)
        stompClient.send("/app/push.send/" + userTo["username"], {}, JSON.stringify(chatMessage))
    messageInput.value = ''
}

const sendMessage = async () => {
    checkInChat()
    let images = document.querySelector("#imageList").files

    const messageInput = document.querySelector('#message')
    const messageContent = messageInput.value.trim();

    const chatMessage = constructMessageObject(messageInput)
    if (messageContent && stompClient) {
        let imageValue
        if (images.length !== 0) {
            let imageConverted = createMessageImage(images)
            await imageConverted.then(async function (value) {
                imageValue = value
            })
        }
        chatMessage.images = imageValue != null ? imageValue : null

        if (userFrom["id"] < userTo["id"])
            stompClient.send("/app/chat.send/" + userFrom["id"] + "/" + userTo["id"], {}, JSON.stringify(chatMessage))
        else
            stompClient.send("/app/chat.send/" + userTo["id"] + "/" + userFrom["id"], {}, JSON.stringify(chatMessage))
    }
    document.querySelector('#imageList').value = ''
}


const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body)
    sendCreatedMessage(message)
    fetchLastMessage(message)
}

const messageControls = document.querySelector('#message-controls')
messageControls.addEventListener('submit', sendMessage, true)


document.getElementsByTagName('textarea')[0].addEventListener("keydown", function (event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendMessage();
    }
});