function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function getCode() {
    let url = window.location.href;
    return url.split('/').pop();
}

function alertInfo(title, text, icon) {
    Swal.fire({
        title: title,
        text: text,
        icon: icon,
    });
}

function alertAndRedirect(title, text, icon, url) {
    Swal.fire({
        title: title,
        text: text,
        icon: icon,
    }).then((result) => {
        let then_result = Object.values(result)[0]
        if (then_result == true) {
            window.location.replace(url);
        }
    })
}

