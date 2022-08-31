'use strict'

let stompClientPostDeleting
let username_post_deleting

function deletingPostLine(postInformation) {
    let post = document.getElementById("comment-" + postInformation).parentNode
    post.parentNode.removeChild(post)
}


const post_deleting_connect = (event) => {
    username_post_deleting = user["username"];
    if (username_post_deleting) {
        const socket = new SockJS('/post-post')
        stompClientPostDeleting = Stomp.over(socket)
        stompClientPostDeleting.connect({}, onPostDeletingConnected, onPostDeletingError)
    }
    event.preventDefault()
}

const onPostDeletingConnected = () => {
    stompClientPostDeleting.subscribe('/topic/postDelete/' + username_post_deleting, onPostDeletingReceived)
}

const onPostDeletingError = (error) => {
    console.log(error)
}



const sendDeletingPost = async (event) => {
    let form = event.target.parentNode.parentNode.parentNode
    let postId = form.querySelectorAll(".timeline-footer")[1].id.split("-")[1]

    stompClientPostDeleting.send("/app/post.delete/" + username_post_deleting, {}, JSON.stringify(postId))
}


const onPostDeletingReceived = (payload) => {
    const post = JSON.parse(payload.body);
    deletingPostLine(post);
}

document.addEventListener('DOMContentLoaded', post_deleting_connect, true)

