let posts = document.getElementsByClassName('post')

let page = 0
let isLoading = true

$(window).scroll(function () {
    if ($(document).height() <= $(window).scrollTop() + $(window).height() + 100 && !isLoading) {
        isLoading = true
        loadPosts()
    }
});

$(function () {
    loadPosts()
})

const setLike = (event) => {
    let likeButton = event.target

    let id = likeButton.parentNode.parentNode.id.split("-")[1]

    jQuery.ajax({
        type: 'POST',
        url: currentLocation + "/postLike/" + id + "/like",
        contentType: 'application/json'
    });

    let countLikes = likeButton.parentNode.parentNode.querySelector(".stats-total")
    if (likeButton.classList.contains("text-danger")) {
        likeButton.classList.remove("text-danger")
        likeButton.classList.add("text-black-50")
        countLikes.innerHTML = parseInt(countLikes.innerHTML) - 1
    } else {
        likeButton.classList.remove("text-black-50")
        likeButton.classList.add("text-danger")
        countLikes.innerHTML = parseInt(countLikes.innerHTML) + 1
    }

}



function loadPosts() {
    let xhr = new XMLHttpRequest()
    let url = "http://localhost:8080/post/" + user.username + "/" + page
    xhr.open("GET", url)
    xhr.onload = function (ev) {
        let jsonResponse = JSON.parse(xhr.responseText);
        if (jsonResponse != null && jsonResponse.length > 0) {
            for (let i = 0; i < jsonResponse.length; i++) {
                let post = document.createElement('div')
                post.classList.add('timeline-body', 'post')
                let images_post = document.createElement('div')
                images_post.classList.add("timeline-footer")
                images_post.addEventListener('click', sendComment);

                let comments_post = document.createElement('div')
                comments_post.classList.add("timeline-footer")
                comments_post.id = "comment-" + jsonResponse[i]["id"]

                let source = user["image"] != null ? '/image/' +
                    user["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'

                let currentSource = currentUser["image"] != null ? '/image/' +
                    currentUser["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'

                let images = jsonResponse[i].images
                let comments = jsonResponse[i].comments
                post.innerHTML =
                    "                              <div class=\"timeline-header\">\n" +
                    "                                 <span class=\"userimage\"><img src=\"" + source + "\" alt=\"\"></span>\n" +
                    "                                 <span class=\"username\">" + user.profileInfo.name + " " + user.profileInfo.surname + "<small></small></span>\n" +
                    "                                 <div class=\"deleteOfButton pull-right\">" +
                    "                                       <span class=\"text-muted\">" + jsonResponse[i].fromNow + "</span>" +
                    "                                 </div>" +
                    "                              </div>\n" +
                    "                              <div class=\"timeline-content\">\n" +
                    "                                 <p>\n" + jsonResponse[i].full_text +
                    "                                 </p>\n" +
                    "                              </div>\n"
                for (let j = 0; j < images.length; j++) {
                    let imgcontainer = document.createElement('div')
                    imgcontainer.classList.add("card-body")
                    let postsource = images[j] != null ? '/image/' +
                        images[j].id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
                    imgcontainer.innerHTML = "<img src=\"" + postsource + "\" alt=\"\">"
                    images_post.innerHTML += imgcontainer.outerHTML
                }
                post.appendChild(images_post)
                post.innerHTML +=
                    "                          <div class=\"timeline-likes\">\n" +
                    "                                 <div class=\"stats-right\">\n" +
                    // "                                    <span class=\"stats-text\">259 Shares</span>\n" +
                    "                                    <span class=\"stats-text\">" + comments.length + " Comments</span>\n" +
                    "                                 </div>\n" +
                    "                                 <div class=\"stats\" id=\"likes-" + jsonResponse[i]["id"] + "\">\n" +
                    "                                    <span class=\"fa-stack fa-fw stats-icon\">\n" +
                    "                                    <a class=\"fa fa-heart fa-stack-2x fa-inverse\"></a>\n" +
                    "                                    </span>\n" +
                    "                                    <span class=\"stats-total\">" + jsonResponse[i]["likes"].length + "</span>\n" +
                    "                                 </div>\n" +
                    "                              </div>\n"


                let like = post.querySelector(".fa-heart")
                let flag = false
                for (let j = 0; j < jsonResponse[i]["likes"].length; j++) {
                    if (jsonResponse[i]["likes"][j]["id"] === currentUser["id"]) {
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
                    "                                    <form class=\"commentForm\" id=\"" + jsonResponse[i]["id"] + "\">\n" +
                    "                                       <div class=\"input-group\">\n" +
                    "                                          <input type=\"text\" class=\"form-control rounded-corner\" placeholder=\"Write a comment...\">\n" +
                    "                                          <span class=\"input-group-btn p-l-10\">\n" +
                    "                                          <button class=\"btn btn-primary f-s-12 rounded-corner\" type=\"submit\">Отправить</button>\n" +
                    "                                          </span>\n" +
                    "                                       </div>\n" +
                    "                                    </form>\n" +
                    "                                 </div>\n" +
                    "                              </div>"

                document.getElementById("postLine").appendChild(post)

                const messageControls = post.querySelector(".commentForm")
                messageControls.addEventListener('submit', sendComment, true)
                post.querySelector(".fa-heart").addEventListener('click', setLike, true)
                if (user.id === currentUser.id) {
                    post.querySelector(".deleteOfButton").innerHTML += "<div class=\"pull-right fa fa-trash\" aria-hidden=\"true\"></div>"
                    post.querySelector(".fa-trash").addEventListener('click', sendDeletingPost, true)
                }
            }
            isLoading = false
            posts = document.getElementsByClassName('post')
            page += 1
        }

    }
    xhr.send()

}




