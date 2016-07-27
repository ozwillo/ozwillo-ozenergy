/**
 *
 */
import React from 'react'
import { render } from 'react-dom'

$(".nav li").removeClass("active");
$('#global').addClass("active");




var App = React.createClass({
  	render: function(){
  		return (
  			<div>
  				Global consumption overview
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);
