$('.share-btn').on('click', function(e) {
    e.preventDefault();
    var $link = $(this);
	//var shareURL = window.location.href;
    var shareURL = "https://www2.deloitte.com/in/en.html";
    var networkType = $link.attr('data-network');
    var networks = {
        facebook: {
            width: 525,
            height: 500,
            plugURL: 'https://www.facebook.com/sharer/sharer.php?u='
        },
        twitter: {
            width: 400,
            height: 500,
            plugURL: 'https://www.twitter.com/share?url='
        },
        linkedin: {
            width: 600,
            height: 473,
            plugURL: 'https://www.linkedin.com/shareArticle?mini=true&url='
        },

        pinterest: {
            width: 600,
            height: 473,
            plugURL: 'https://pinterest.com/pin/create/button/?url='
        },
		reddit: {
            width: 600,
            height: 473,
            plugURL: 'http://reddit.com/submit?url='
        },
		vk: {
            width: 600,
            height: 473,
            plugURL: 'http://vkontakte.ru/share.php?url='
        }

    };

    var popup = function(network) {
        var href = networks[network].plugURL + shareURL;
        var options = 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,';
        let height = networks[network].height;
        let width = networks[network].width;
        let top = (window.innerHeight / 2) - height / 2;
        let left = (window.innerWidth / 2) - width / 2;
        window.open(href, '', options + 'height=' + height + ',width=' + width + ',top=' + top + ',left=' + left);
    }
    if (networks[networkType] != undefined) {
        popup(networkType);
    } 
});


