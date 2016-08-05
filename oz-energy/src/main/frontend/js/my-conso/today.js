import React from 'react'
import { render } from 'react-dom'


export class Today extends React.Component{
	render(){
		var d = new Date();
		var date = d.toDateString();
		var time = d.toTimeString();
		return(
			<div>
				<p>We are {date}.</p>
				<p>It is {time.slice(0,5)}.</p>
			</div>
		);
	}

}