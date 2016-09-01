/**
 *
 */
import React from 'react'
import { render } from 'react-dom'

import '../css/specific.css'

$(".nav li").removeClass("active");
$('#home').addClass("active");




var presentation = "Energy consumption monitoring for consumers, providers and territories"


var App = React.createClass({
  	render: function(){
  		return (
  			<div className="body-content">
				<div className="jumbotron">
					<h1>Oz'Energy</h1>
					<p>{presentation}</p>
				</div>
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);
