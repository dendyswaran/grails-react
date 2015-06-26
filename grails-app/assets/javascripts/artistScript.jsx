var ArtistItem = React.createClass({
	render: function() {
		return (
			<li>
				 {this.props.artistName.toString()}
			</li>
		);
	}
});

var ArtistList = React.createClass({
	render: function() {
		var ArtistNodes = this.props.artistList.map(function(artist, index) {
			return (
				<ArtistItem key={index} 
					        artistName={artist.artistName} />
			);
		});
		
		return (
            <ul>
                {ArtistNodes}
            </ul>
        );
	}
});

var renderFromServer = function(obj) {
    var data = Java.from(obj);
    return React.renderToString(
        <ArtistList artistList={data} />
    );
};

var renderFromClient = function(obj) {
	var data = obj || [];
	React.render(
         <ArtistList artistList={data} />,
        document.getElementById("content")
    );
}