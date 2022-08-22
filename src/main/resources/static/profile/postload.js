

let posts = document.getElementsByClassName('post')

let page = 0
let isLoading = false

$(window).scroll(function () {
    if ($(document).height() <= $(window).scrollTop() + $(window).height() + 100 && !isLoading) {
        isLoading = true
        loadPosts()
    }
});

$(function () {
    loadPosts()
})

function loadPosts() {
    console.log("-----")
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
                    "                                 <span class=\"userimage\"><img src=\""+source +"\" alt=\"\"></span>\n" +
                    "                                 <span class=\"username\">" + profileInfo.name + " " + profileInfo.surname + "<small></small></span>\n" +
                    "                                 <span class=\"pull-right text-muted\">" + jsonResponse[i].fromNow + "</span>" +
                    "                              </div>\n" +
                    "                              <div class=\"timeline-content\">\n" +
                    "                                 <p>\n" + jsonResponse[i].full_text +
                    "                                 </p>\n" +
                    "                              </div>\n"
                for (let j = 0; j < images.length; j++) {
                    let imgcontainer = document.createElement('div')
                    imgcontainer.classList.add("card-body")
                    let postsource = images[j].image != null ? '/image/' +
                        images[j].image.id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
                    imgcontainer.innerHTML = "<img src=\"" + postsource + "\" alt=\"\">"
                    images_post.innerHTML += imgcontainer.outerHTML
                }
                post.appendChild(images_post)
                post.innerHTML +=
                        "                          <div class=\"timeline-likes\">\n" +
                    "                                 <div class=\"stats-right\">\n" +
                    // "                                    <span class=\"stats-text\">259 Shares</span>\n" +
                    "                                    <span class=\"stats-text\">21 Comments</span>\n" +
                    "                                 </div>\n" +
                    "                                 <div class=\"stats\">\n" +
                    "                                    <span class=\"fa-stack fa-fw stats-icon\">\n" +
                    "                                    <i class=\"fa fa-circle fa-stack-2x text-danger\"></i>\n" +
                    "                                    <i class=\"fa fa-heart fa-stack-1x fa-inverse t-plus-1\"></i>\n" +
                    "                                    </span>\n" +
                    "                                    <span class=\"stats-total\">4.3k</span>\n" +
                    "                                 </div>\n" +
                    "                              </div>\n"

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

                post.innerHTML +=    "               <div class=\"timeline-comment-box\">\n" +
                    "                                 <div class=\"user\"><img src=\"" + currentSource + "\"></div>\n" +
                    "                                 <div class=\"input\">\n" +
                    "                                    <form class=\"commentForm\" id=\"" + jsonResponse[i]["id"]+ "\">\n" +
                    "                                       <div class=\"input-group\">\n" +
                    "                                          <input type=\"text\" class=\"form-control rounded-corner\" placeholder=\"Write a comment...\">\n" +
                    "                                          <span class=\"input-group-btn p-l-10\">\n" +
                    "                                          <button class=\"btn btn-primary f-s-12 rounded-corner\" type=\"submit\">Comment</button>\n" +
                    "                                          </span>\n" +
                    "                                       </div>\n" +
                    "                                    </form>\n" +
                    "                                 </div>\n" +
                    "                              </div>"

                document.getElementById("postLine").appendChild(post)

                const messageControls = post.querySelector(".commentForm")
                messageControls.addEventListener('submit', sendComment, true)
            }
            isLoading = false
            posts = document.getElementsByClassName('post')
            page += 1
        }

    }
    xhr.send()

}




