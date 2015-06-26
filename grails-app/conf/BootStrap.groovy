import com.grailsreact.Artist

class BootStrap {

    def init = { servletContext ->
	
		(0..10).each { 
			new Artist(artistName: "Artist ${it}").save(flush: true)
		}
	
    }
    def destroy = {
    }
}
