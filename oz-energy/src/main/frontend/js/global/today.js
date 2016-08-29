import React from 'react'
import { render } from 'react-dom'


export class Today extends React.Component{
	constructor(props) {
		super(props);
		this.state = {
				date: new Date().toLocaleDateString(),
				time: new Date().toLocaleTimeString()
		}
	}
	
	componentDidMount = () => {
		this.timerId = setInterval(this.update_time, 500);
	}
	componentWillUnmount = () => {
		clearInterval(this.timerId);
	}
	update_time = () => {
		this.setState({date: new Date().toLocaleDateString(),
				time: new Date().toLocaleTimeString()});
	}
	
	render(){

		return(
			<div>
				<p>We are {this.state.date}.</p>
				<p>It is {this.state.time.slice(0,5)}.</p>
			</div>
		);
	}

}