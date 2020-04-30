document.getElementById("resourcepath").style.display = "none";
$(document).ready(
		function() {
			$('#submit').click(
					function() {
						var failure = function(err) {
							console.log("Unable to get data in Search Component: " + err);
						};
						var keyword = $('#keyword').val();
						var resourcepath = $('#resourcepath').text();
						var table = $(this).closest('table');
						$.ajax({
							type : 'POST',
							url : resourcepath + '.json',
							data : 'keyword=' + keyword,
							success : function(data) {
								if (data) {
									var len = data.length;
									var txt = "";
									if (len > 0) {
										for (var i = 0; i < len; i++) {
											if (data[i].index && data[i].path) {
												txt += "<tr><td>"
														+ data[i].index
														+ "</td><td>"
														+ data[i].path
														+ "</td></tr>";
											}
										}
										if (txt != "") {
											$("#customers").append(txt)
													.removeClass("hidden");
										}
									}
								}
							},
							error : function(msg) {
								console.log("Error in Search Component AJAX: "
										+ msg);
							}
						});
					});
		});
