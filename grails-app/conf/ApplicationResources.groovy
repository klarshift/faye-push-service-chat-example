modules = {
	bootstrap {
		resource url:'bootstrap/css/bootstrap.min.css'
		resource url:'bootstrap/js/bootstrap.min.js'
	}
	
    application {
		dependsOn 'jquery,bootstrap'
        resource url:'js/application.js'
		resource url:'css/style.css'
    }
}