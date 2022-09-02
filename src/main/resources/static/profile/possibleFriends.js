let possibleFriendsContainer = document.getElementById("possibleFriendsContainer")

$(function (){
    if (currentUser.id === user.id)
        loadPossibleFriends()
})
function loadPossibleFriends() {
   jQuery.ajax({
       type: "GET",
       url: currentLocation + "/possibleFriends/" + user["username"],
       contentType: "application/json",
       success: createPossibleFriends
   })
}

function addPossibleFriend(event){
    const possibleFriendBlock = event.currentTarget.parentNode
    let friendId = event.currentTarget.parentNode.id.split("-")[1]
    console.log(friendId)
    jQuery.ajax({
        type: "GET",
        url: currentLocation + "/addPossibleFriend/" + friendId,
        contentType: "application/json",
        success: removePossibleFriend(possibleFriendBlock)
    })
}

function removePossibleFriend(possibleFriendBlock){
    possibleFriendBlock.parentNode.removeChild(possibleFriendBlock)
}


function createPossibleFriends(data){
    let possibleFriends = data[0]
    let quantityOfMutualFriends = data[1]
    for (let i = 0; i < possibleFriends.length; i++) {
        let friend = possibleFriends[i]
        let count = quantityOfMutualFriends[i]

        let source = friend["image"] != null ? '/image/' +
            friend["image"]["id"] : 'https://bootdey.com/img/Content/avatar/avatar6.png'

        const friendHTML = document.createElement("div")
        friendHTML.id = "possibleFriend-" + friend["id"]
        friendHTML.classList.add("d-flex", "justify-content-between", "mb-2", "pb-2", "border-bottom")
        friendHTML.innerHTML = "<a href=\"/user/" + friend["username"] + "\" class=\"d-flex align-items-center hover-pointer\">\n" +
            "                                        <img class=\"img-xs rounded-circle\"\n" +
            "                                             src=\"" + source + "\" alt=\"\">\n" +
            "                                        <div class=\"ml-2\">\n" +
            "                                            <div class=\"mb-1 possibleFriends\">" + friend["profileInfo"]["name"] + " " + friend["profileInfo"]["surname"] + "</div>\n" +
            "                                            <div class=\"text-muted quantityOfPossibleFriends\">Общих друзей " + count + "</div>\n" +
            "                                        </div>\n" +
            "                                    </a>\n" +
            "                                    <button class=\"btn btn-icon addButton\">\n" +
            "                                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\"\n" +
            "                                             viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"\n" +
            "                                             stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
            "                                             class=\"feather feather-user-plus\" data-toggle=\"tooltip\" title=\"\"\n" +
            "                                             data-original-title=\"Connect\">\n" +
            "                                            <path d=\"M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2\"></path>\n" +
            "                                            <circle cx=\"8.5\" cy=\"7\" r=\"4\"></circle>\n" +
            "                                            <line x1=\"20\" y1=\"8\" x2=\"20\" y2=\"14\"></line>\n" +
            "                                            <line x1=\"23\" y1=\"11\" x2=\"17\" y2=\"11\"></line>\n" +
            "                                        </svg>\n" +
            "                                    </button>"
        friendHTML.querySelector(".addButton").addEventListener('click', addPossibleFriend,true)
        possibleFriendsContainer.append(friendHTML)
    }
}