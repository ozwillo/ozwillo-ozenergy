/**
 *
 */
import React from 'react'
import { render } from 'react-dom'

import '../css/specific.css'

$(".nav li").removeClass("active");
$('#home').addClass("active");

var App = React.createClass({
  	render: function(){
  		return (
  			<div className="body-content">
  				Index
  			</div>
  		);
  	}
  });

ReactDOM.render(
		<App/>,
		document.getElementById('container')
);
