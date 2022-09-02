'use strict'

let stompClient
let username
let commentPage = 1


function loadMoreComments(post) {
    let xhr = new XMLHttpRequest()
    let url = "http://localhost:8080/" + post.id + "/comment/" + commentPage
    xhr.open("GET", url)
    xhr.onload = function (ev) {
        let jsonResponse = JSON.parse(xhr.responseText);
        for (let i = 0; i < jsonResponse.length; i++) {
            createCommentLine(jsonResponse[i])
        }
    }
    xhr.send()
    commentPage++
}

function createCommentLine(comment) {
    let commentsContainer = document.createElement('li')
    commentsContainer.classList.add("media")
    let userImage = comment["user"].image != null ? '/image/' +
        comment["user"].image.id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
    commentsContainer.innerHTML +=
        "                                <div class=\"profile-picture bg-gradient bg-primary mb-4\">\n" +
        "                                    <img src=\"" + userImage + "\" width=\"44\" height=\"44\">\n" +
        "                                </div>\n" +
        "                                <div class=\"media-body\">\n" +
        "                                    <div class=\"media-title mt-0 mb-1\">\n" +
        "                                        <a href=\"/user/" + comment["user"]["username"] + "\">" + currentUser["profileInfo"]["name"] + " " + currentUser["profileInfo"]["surname"] + "</a> <small>" + comment["time"] + "</small>\n" +
        "                                    </div>\n" +
        comment["content"] +
        "                                 </div> "
    const postForComment = document.getElementById("comment-" + comment["post"]["id"])
    const showMoreButton = postForComment.querySelector('.showMore')
    postForComment.appendChild(commentsContainer)
    const button = document.createElement('div')
    button.classList.add('text-center', 'media')
    button.appendChild(showMoreButton)
    postForComment.appendChild(button)
}


const connect = (event) => {
    username = user["username"];
    if (username) {
        const socket = new SockJS('/post-comments')
        stompClient = Stomp.over(socket)
        stompClient.connect({}, onConnected, onError)
    }
    event.preventDefault()
}

const onConnected = () => {
    stompClient.subscribe('/topic/post/comment/' + username, onMessageReceived)
}

const onError = (error) => {
    console.log(error)
}

const sendComment = (event) => {
    let form = event.target
    const messageInput = form.querySelector(".form-control");

    const messageContent = messageInput.value.trim();
    let d = new Date();
    let ye = new Intl.DateTimeFormat('ru', {year: 'numeric'}).format(d);
    let mo = new Intl.DateTimeFormat('ru', {month: '2-digit'}).format(d);
    let da = new Intl.DateTimeFormat('ru', {day: '2-digit'}).format(d);
    let time = new Intl.DateTimeFormat('ru',
        {
            hour: "numeric",
            minute: "numeric",
        }).format(d)

    console.log(`${da}/${mo}/${ye} ${time}`)

    if (messageContent && stompClient) {
        let xhr = new XMLHttpRequest()
        let url = "http://localhost:8080/post/" + form.id
        xhr.open("GET", url)
        xhr.onload = function (ev) {
            let jsonResponse = JSON.parse(xhr.responseText);
            const postComment = {
                post: jsonResponse,
                user: currentUser,
                content: messageInput.value,
                time: `${da}/${mo}/${ye} ${time}`
            }
            stompClient.send("/app/comment.send/" + username, {}, JSON.stringify(postComment))
            messageInput.value = ''
        }
        xhr.send()
    }
    event.preventDefault();
}


const onMessageReceived = (payload) => {
    const comment = JSON.parse(payload.body);
    createCommentLine(comment);
}

document.addEventListener('DOMContentLoaded', connect, true)

