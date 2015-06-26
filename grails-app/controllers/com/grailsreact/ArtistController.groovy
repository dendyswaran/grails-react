package com.grailsreact

import grails.converters.JSON


class ArtistController {

    def index() {
		[artistList: Artist.list()]
    }
	
	def getArtists() {
		def artistList = Artist.list()
		render artistList as JSON
	}
}
