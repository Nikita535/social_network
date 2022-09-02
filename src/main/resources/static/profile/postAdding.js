'use strict'

let stompClientPost
let username_post

function createPostLine(postInformation) {
    let post = document.createElement('div')
    post.classList.add('timeline-body', 'post')
    let images_post = document.createElement('div')
    images_post.classList.add("timeline-footer")
    images_post.addEventListener('click', sendComment);

    let comments_post = document.createElement('div')
    comments_post.classList.add("timeline-footer")
    comments_post.id = "comment-" + postInformation["id"]

    let source = user["image"] != null ? '/image/' +
        user["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'

    let currentSource = currentUser["image"] != null ? '/image/' +
        currentUser["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'

    let images = postInformation.images
    let comments = postInformation.comments
    post.innerHTML =
        "                              <div class=\"timeline-header\">\n" +
        "                                 <span class=\"userimage\"><img src=\"" + source + "\" alt=\"\"></span>\n" +
        "                                 <span class=\"username\">" + user.profileInfo.name + " " + user.profileInfo.surname + "<small></small></span>\n" +
        "                                 <div class=\"deleteOfButton pull-right\">" +
        "                                       <span class=\"text-muted\">" + postInformation.fromNow + "</span>" +
        "                                 </div>" +
        "                              </div>\n" +
        "                              <div class=\"timeline-content\">\n" +
        "                                 <p>\n" + postInformation.full_text +
        "                                 </p>\n" +
        "                              </div>\n"
    if (images != null) {
        for (let j = 0; j < images.length; j++) {
            let imgcontainer = document.createElement('div')
            imgcontainer.classList.add("card-body")
            let postsource = images[j] != null ? '/image/' +
                images[j].id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
            imgcontainer.innerHTML = "<img src=\"" + postsource + "\" alt=\"\">"
            images_post.innerHTML += imgcontainer.outerHTML
        }
    }
    post.appendChild(images_post)
    post.innerHTML +=
            "                          <div class=\"timeline-likes\">\n" +
            "                                 <div class=\"stats-right\">\n" +
            // "                                    <span class=\"stats-text\">259 Shares</span>\n" +
            "                                    <span class=\"stats-text\">" + comments.length + " Comments</span>\n" +
            "                                 </div>\n" +
            "                                 <div class=\"stats\" id=\"likes-" + postInformation["id"] + "\">\n" +
            "                                    <span class=\"fa-stack fa-fw stats-icon\">\n" +
            "                                    <a class=\"fa fa-heart fa-stack-2x fa-inverse\"></a>\n" +
            "                                    </span>\n" +
            "                                    <span class=\"stats-total\">" + postInformation["likes"].length + "</span>\n" +
            "                                 </div>\n" +
            "                              </div>\n"


    let like = post.querySelector(".fa-heart")
    let flag = false
    for (let j = 0; j < postInformation["likes"].length; j++) {
        if (postInformation["likes"][j]["id"] === currentUser["id"]) {
            flag = true
            break
        }
    }
    if (flag)
        like.classList.add("text-danger")
    else
        like.classList.add("text-black-50")


    for (let j = 0; j < comments.length; j++) {
        let commentsContainer = document.createElement('li')
        commentsContainer.classList.add("media")
        let userImage = comments[j]["user"].image != null ? '/image/' +
            comments[j]["user"].image.id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
        commentsContainer.innerHTML +=
            "                                <div class=\"profile-picture bg-gradient bg-primary mb-4\">\n" +
            "                                    <img src=\"" + userImage + "\" width=\"44\" height=\"44\">\n" +
            "                                </div>\n" +
            "                                <div class=\"media-body\">\n" +
            "                                    <div class=\"media-title mt-0 mb-1\">\n" +
            "                                        <a href=\"#\">" + comments[j]["user"]["username"] + "</a> <small>" + comments[j]["time"] + "</small>\n" +
            "                                    </div>\n" +
            comments[j]["content"] +
            "                                 </div> "
        comments_post.appendChild(commentsContainer)
    }

    post.appendChild(comments_post)

    post.innerHTML += "               <div class=\"timeline-comment-box\">\n" +
        "                                 <div class=\"user\"><img src=\"" + currentSource + "\"></div>\n" +
        "                                 <div class=\"input\">\n" +
        "                                    <form class=\"commentForm\" id=\"" + postInformation["id"] + "\">\n" +
        "                                       <div class=\"input-group\">\n" +
        "                                          <input type=\"text\" class=\"form-control rounded-corner\" placeholder=\"Write a comment...\">\n" +
        "                                          <span class=\"input-group-btn p-l-10\">\n" +
        "                                          <button class=\"btn btn-primary f-s-12 rounded-corner\" type=\"submit\">Отправить</button>\n" +
        "                                          </span>\n" +
        "                                       </div>\n" +
        "                                    </form>\n" +
        "                                 </div>\n" +
        "                              </div>"

    // post после
    if (currentUser["id"] === user["id"]) {
        const createLine = document.getElementById("postLine").firstChild["nextSibling"]
        createLine.parentNode.insertBefore(post, createLine.nextSibling);
    } else {
        const createLine = document.getElementById("postLine")
        createLine.insertBefore(post, createLine.firstChild)
    }

    const messageControls = post.querySelector(".commentForm")
    messageControls.addEventListener('submit', sendComment, true)
    post.querySelector(".fa-heart").addEventListener('click', setLike, true)

    if (user.id === currentUser.id) {
        post.querySelector(".deleteOfButton").innerHTML += "<div class=\"pull-right fa fa-trash\" aria-hidden=\"true\"></div>"
        post.querySelector(".fa-trash").addEventListener('click', sendDeletingPost, true)
    }
}


const post_connect = (event) => {
    username_post = user["username"];
    if (username_post) {
        const socket = new SockJS('/post-post')
        stompClientPost = Stomp.over(socket)
        stompClientPost.connect({}, onPostConnected, onPostError)
    }
    event.preventDefault()
}

const onPostConnected = () => {
    stompClientPost.subscribe('/topic/post/' + username_post, onPostReceived)
}

const onPostError = (error) => {
    console.log(error)
}

async function createPostImage(images) {
    var request = {};
    let elem;
    request.images = images; // some data

    let formData = new FormData();
    for (let i = 0; i < images.length; i++) {
        formData.append("file", images[i]);
    }
    const response = await fetch(currentLocation + "/post/create", {
        method: "POST",
        body: formData,
    }).then((data) => {
        elem = data.json();
    })
    return elem
}


const sendPost = async (event) => {
    let form = event.target
    const PostInput = form.parentNode.previousSibling["previousElementSibling"];

    const PostContent = PostInput.value.trim();

    let images = form.parentNode.querySelector("#imageList").files

    if (PostContent && stompClientPost) {
        let imageValue
        console.log(images)
        if (images.length !== 0) {
            let imageConverted = createPostImage(images)
            await imageConverted.then(async function (value) {
                imageValue = value
            })
        }

        const post = {
            user: currentUser,
            full_text: PostInput.value,
            images: imageValue != null ? imageValue : null
        }

        stompClientPost.send("/app/post.send/" + username_post, {}, JSON.stringify(post))
        PostInput.value = ''
    }

    form.parentNode.querySelector("#imageList").value = ''
    event.preventDefault();
}


const onPostReceived = (payload) => {
    const comment = JSON.parse(payload.body);
    createPostLine(comment);
}

document.addEventListener('DOMContentLoaded', post_connect, true)

