class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		"/about"(view: "/about")

		"/"(controller:"chat")
		"500"(view:'/error')
	}
}
