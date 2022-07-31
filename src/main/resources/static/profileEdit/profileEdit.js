let name=document.getElementById("profileFirstName").value
let surname=document.getElementById("profileLastName").value
let city=document.getElementById("profileAddress").value
let website=document.getElementById("profileCompany").value
let desc=document.getElementById("profileDescription").value
let crossbar=document.querySelector(".progress-bar")
let crossbarValue=document.querySelector(".pull-right")
let file=document.getElementById("file")

let arr=[name,surname,city,website,desc,file]

let count=0
for (let i = 0; i < arr.length; i++) {
    if(arr[i].length!==0){
        count++
    }
}


crossbar.style.width=Math.floor(count/arr.length *100) + "%"
crossbarValue.textContent=Math.floor(count/arr.length *100) + "%"

