let posts = document.getElementsByClassName('post')

let page = 1

$(window).scroll(function () {
    if ($(document).height() <= $(window).scrollTop() + $(window).height()) {
        loadPosts()
    }
});



function loadPosts() {
    let xhr = new XMLHttpRequest()
    let url = "http://localhost:8080/post/" + user.username + "/" + page
    xhr.open("GET", url)
    xhr.onload = function (ev) {
        let jsonResponse = JSON.parse(xhr.responseText);
        console.log(jsonResponse);
        if (jsonResponse != null && jsonResponse.length > 0) {
            for (let i = 0; i < jsonResponse.length; i++) {
                let post = document.createElement('div')
                post.classList.add('row', 'post')
                let ediv1 = document.createElement('div')
                ediv1.classList.add("col-md-12", "grid-margin")
                let ediv2 = document.createElement('div')
                ediv2.classList.add("card", "rounded")
                let source = user["image"] != null ? '/image/' +
                    user["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'
                let images = jsonResponse[i].images
                ediv2.innerHTML =
                    "                            <div class=\"card-header\">\n" +
                    "                                <div class=\"d-flex align-items-center justify-content-between\">\n" +
                    "                                    <div class=\"d-flex align-items-center\">\n" +
                    "                                        <img src=\"" + source + "\" class=\"img-xs rounded-circle\"\n" +
                    "                                             alt=\"\">\n" +
                    "                                        <div class=\"ml-2\">\n" +
                    "                                            <p>" + profileInfo.name + " " + profileInfo.surname + "</p>\n" +
                    "                                            <p class=\"tx-11 text-muted\">" + jsonResponse[i].fromNow +
                    "                                                </p>\n" +
                    "                                        </div>\n" +
                    "                                    </div>\n" +
                    "                                </div>\n" +
                    "                            </div>" +
                    "                            <div class=\"card-body\">\n" +
                    "                                <p class=\"mb-3 tx-14\">" + jsonResponse[i].full_text + "</p>\n" +
                    "                            </div>"
                for (let j = 0; j < images.length; j++) {
                    let imgcontainer = document.createElement('div')
                    imgcontainer.classList.add("card-body")
                    let postsource = images[j].image != null ? '/image/' +
                        images[j].image.id : 'https://bootdey.com/img/Content/avatar/avatar6.png'
                    imgcontainer.innerHTML = "<img src=\"" + postsource + "\" alt=\"\">"
                    ediv2.innerHTML += imgcontainer.outerHTML
                }
                ediv2.innerHTML += "<div class=\"card-footer\">\n" +
                    "                                <div class=\"d-flex post-actions\">\n" +
                    "                                    <a href=\"javascript:\" class=\"d-flex align-items-center text-muted mr-4\">\n" +
                    "                                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\"\n" +
                    "                                             viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"\n" +
                    "                                             stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
                    "                                             class=\"feather feather-heart icon-md\">\n" +
                    "                                            <path d=\"M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z\"></path>\n" +
                    "                                        </svg>\n" +
                    "                                        <p class=\"d-none d-md-block ml-2\">Like</p>\n" +
                    "                                    </a>\n" +
                    "                                    <a href=\"javascript:\" class=\"d-flex align-items-center text-muted mr-4\">\n" +
                    "                                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\"\n" +
                    "                                             viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"\n" +
                    "                                             stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
                    "                                             class=\"feather feather-message-square icon-md\">\n" +
                    "                                            <path d=\"M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z\"></path>\n" +
                    "                                        </svg>\n" +
                    "                                        <p class=\"d-none d-md-block ml-2\">Comment</p>\n" +
                    "                                    </a>\n" +
                    "                                    <a href=\"javascript:\" class=\"d-flex align-items-center text-muted\">\n" +
                    "                                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\"\n" +
                    "                                             viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"\n" +
                    "                                             stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
                    "                                             class=\"feather feather-share icon-md\">\n" +
                    "                                            <path d=\"M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8\"></path>\n" +
                    "                                            <polyline points=\"16 6 12 2 8 6\"></polyline>\n" +
                    "                                            <line x1=\"12\" y1=\"2\" x2=\"12\" y2=\"15\"></line>\n" +
                    "                                        </svg>\n" +
                    "                                        <p class=\"d-none d-md-block ml-2\">Share</p>\n" +
                    "                                    </a>\n" +
                    "                                </div>\n" +
                    "                            </div>\n"
                ediv1.appendChild(ediv2)
                post.appendChild(ediv1)
                document.getElementById("postLine").appendChild(post)
            }
            posts = document.getElementsByClassName('post')
            page += 1
        }

    }
    xhr.send()

}
