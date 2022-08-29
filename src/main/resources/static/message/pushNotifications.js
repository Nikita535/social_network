'use strict'

let pushStompClient
let pushUsername
let push_container = document.getElementById("push-notification")


function createPushLine(message) {
    console.log(message)
    const flexBox = document.createElement('div')
    flexBox.classList.add('toast', 'fade', 'show')

    const toast_header = document.createElement('div')
    toast_header.classList.add("toast-header")
    toast_header.innerHTML +="                    <strong class=\"mr-auto\">" + message["userFrom"]["profileInfo"]["name"] +
        " " + message["userFrom"]["profileInfo"]["surname"]+ "</strong>\n" +
    "                    <small class=\"text-muted\">" + message["time"].split(" ")[1] + "</small>\n" +
    "                    <button type=\"button\" class=\"ml-2 mb-1 close\" data-dismiss=\"toast\" aria-label=\"Close\">\n" +
    "                        <span aria-hidden=\"true\">&times;</span>\n" +
    "                    </button>\n"

    flexBox.append(toast_header)

    flexBox.innerHTML +=
        "                <div class=\"toast-body\">\n" +
        "                    See? Just like this.\n" +
        "                </div>"
    flexBox.querySelector(".toast-body").innerHTML = message["content"]

    flexBox.querySelector(".close").addEventListener('click', function (event) {
        flexBox.classList.remove("show")
        flexBox.classList.add("hide")
    })

    setTimeout(function () {
        flexBox.classList.add("hide")
    }, 10000);

    push_container.append(flexBox)
}



const pushConnect = () => {
    pushUsername = userFrom["username"];
    if (pushUsername) {
        const socket = new SockJS('/push-notifications')
        pushStompClient = Stomp.over(socket)
        pushStompClient.connect({}, onPushConnected, onPushError)
    }
}


const onPushConnected = () => {
    pushStompClient.subscribe("/topic/push/" + userFrom["username"], onPushReceived)
}

const onPushError = (error) => {
    console.log(error)
}

const onPushReceived = (payload) => {
    const message = JSON.parse(payload.body);
    createPushLine(message);
    fetchLastMessage(message)
}

document.addEventListener('DOMContentLoaded', pushConnect, true)
