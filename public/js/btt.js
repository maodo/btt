//========
// main.js
//========

 $(document).ready(function() {

	$('#home-navbar ul.nav li').removeClass("active");
	$('#home-navbar ul.nav > li > a[href="' + document.location.pathname + '"]').parent().addClass('active');
	$().alert();
});