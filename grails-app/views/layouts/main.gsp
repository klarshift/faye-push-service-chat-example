<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title><g:layoutTitle default="klarshift - Realtime Chat Demo with faye.js and Grails" /></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<r:require module="application" />
<g:layoutHead />
<r:layoutResources />
</head>
<body>
	<div id="page">
		<div class="container">
			<div class="navbar navbar-inverse"">
				<div class="navbar-inner">
					<div class="container">
						<a class="btn btn-navbar" data-toggle="collapse"
							data-target=".subnav-collapse"> <span class="icon-bar"></span>
							<span class="icon-bar"></span> <span class="icon-bar"></span>
						</a> <a class="brand" href="${g.createLink(uri: '/') }">klarshift - Realtime Chat</a>
						<div class="nav-collapse subnav-collapse">
							<ul class="nav">
								<li class="${controllerName == 'chat' ? 'active' : '' }"><a href="${g.createLink(uri: '/') }">Home</a></li>
								<li class="${controllerName != 'chat' ? 'active' : '' }"><a href="${g.createLink(uri: '/about') }">About</a></li>								
							</ul>						
						</div>
						
						<!-- /.nav-collapse -->
					</div>
				</div>
				<!-- /navbar-inner -->
			</div>
			<g:layoutBody />
		</div>
	</div>
	<r:layoutResources />
</body>
</html>