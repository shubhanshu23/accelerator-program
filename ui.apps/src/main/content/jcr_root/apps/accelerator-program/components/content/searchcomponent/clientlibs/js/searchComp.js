document.getElementById("resourcepath").style.display = "none";
$(document).ready(function() {
	$('#submit').click(function() {
		var failure = function(err) {
			console.log("Unable to get data in Search Component: " + err);
		};
		event.preventDefault();
		var keyword = $('#keyword').val();
		var resourcepath = $('#resourcepath').text();
		var txt;
		$.ajax({
			type : 'POST',
			url : resourcepath + '.json',
			data : 'keyword=' + keyword + '&resourcepath=' + resourcepath,
			success : function(data) {
				$.each(data, function(key, val) {
					if (val.index && val.path) {
						txt += "<tr>";
						txt += "<td>" + val.index + "</td>";
						txt += "<td>" + val.path + "</td>";
						txt += "<td>" + val.pageTitle + "</td>";
						txt += "<td>" + val.pageDescription + "</td>";
						txt += "<td>" + val.assetTitle + "</td>";
						txt += "<td>" + val.assetDescription + "</td>";
						txt += "</tr>";
					}
				});
				if (txt != "") {
					$("#customers").append(txt);
				}
			},
			error : function(msg) {
				console.log("Error in Search Component AJAX: " + msg);
			}
		});
	});
});
