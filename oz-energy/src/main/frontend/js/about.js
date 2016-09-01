import React from 'react'
import { render } from 'react-dom'

import '../css/specific.css'

$(".nav li").removeClass("active");
$('#about').addClass("active");

var App = React.createClass({
  	render: function(){
  		return (
  			<div>
  				<div className="well well-lg">
  					<p>Oz'Energy is an <a href="http://openwide.fr/">Open Wide</a> application available on Ozwillo.</p>
  					<p>For more information about Ozwillo go to <a href="http://openwide.fr/">ozwillo.com</a>.</p>
  					<p>Oz'Energy takes part into the OCCIware project.</p>
  					<p>For more information about the OCCIware project got to <a href="http://www.occiware.org/bin/view/Main/">occiware.org</a>.</p>
  				</div>
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);