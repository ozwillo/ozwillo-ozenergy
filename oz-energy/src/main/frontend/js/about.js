import React from 'react'
import { render } from 'react-dom'

$(".nav li").removeClass("active");
$('#about').addClass("active");

var App = React.createClass({
  	render: function(){
  		return (
  			<div>
  				about
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);