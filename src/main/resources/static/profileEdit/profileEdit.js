let name=document.getElementById("profileFirstName").value
let surname=document.getElementById("profileLastName").value
let city=document.getElementById("profileAddress").value
let website=document.getElementById("profileCompany").value
let desc=document.getElementById("profileDescription").value
let crossbar=document.querySelector(".progress-bar")
let crossbarValue=document.querySelector(".pull-right")

let arr=[name,surname,city,website,desc]

let count=0
for (let i = 0; i < arr.length; i++) {
    if(arr[i].length!==0){
        count++
    }
}


crossbar.style.width=count/arr.length *100 + "%"
crossbarValue.textContent=count/arr.length *100 + "%"

