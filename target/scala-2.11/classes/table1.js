/**
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only. Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

var Comment = React.createClass({displayName: "Comment",
    render: function() {
        var rawMarkup = marked(this.props.children.toString(), {sanitize: true});
        return (
            React.createElement("div", {className: "comment"},
                React.createElement("h2", {className: "commentAuthor"},
                    this.props.author
                ),
                React.createElement("span", {dangerouslySetInnerHTML: {__html: rawMarkup}})
            )
        );
    }
});

var CommentBox = React.createClass({displayName: "CommentBox",
    loadCommentsFromServer: function() {
        $.ajax({
            url: this.props.url,
            dataType: 'json',
            cache: false,
            success: function(data) {
                this.setState({data: data});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleCommentSubmit: function(comment) {
        var comments = this.state.data;
        //comments.push(comment);
        this.setState({data: comments}, function() {
            // `setState` accepts a callback. To avoid (improbable) race condition,
            // we'll send the ajax request right after we optimistically set the new
            // state.
            $.ajax({
                url: "http://127.0.0.1:8083/products",
                dataType: 'json',
                //beforeSend: function (request)
                //{
                //    request.setRequestHeader("Access-Control-Allow-Origin", "http://localhost:63342");
                //},
                type: 'POST',
                data: comment,
                success: function(data) {
                    this.setState({data: data});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.url, status, err.toString());
                }.bind(this)
            });
        });
    },
    getInitialState: function() {
        return {data: []};
    },
    componentDidMount: function() {
        this.loadCommentsFromServer();
        setInterval(this.loadCommentsFromServer, this.props.pollInterval);
    },
    render: function() {
        return (
            React.createElement("div", {className: "commentBox"},
                React.createElement("h1", null, "Comments"),
                React.createElement(CommentList, {data: this.state.data}),
                React.createElement(CommentForm, {onCommentSubmit: this.handleCommentSubmit})
            )
        );
    }
});

var CommentList = React.createClass({displayName: "CommentList",
    render: function() {
        var commentNodes = this.props.data.map(function(comment, index) {
            return (
                // `key` is a React-specific concept and is not mandatory for the
                // purpose of this tutorial. if you're curious, see more here:
                // http://facebook.github.io/react/docs/multiple-components.html#dynamic-children
                React.createElement(Comment, {author: comment.author, key: index},
                    comment.text
                )
            );
        });
        return (
            React.createElement("div", {className: "commentList"},
                commentNodes
            )
        );
    }
});

var CommentForm = React.createClass({displayName: "CommentForm",
    handleSubmit: function(e) {
        e.preventDefault();
        var author = React.findDOMNode(this.refs.author).value.trim();
        var text = React.findDOMNode(this.refs.text).value.trim();
        if (!text || !author) {
            return;
        }
        this.props.onCommentSubmit({name: author, price: text, date:"2015-07-09", labels:[{name:"молочное"}]});
        React.findDOMNode(this.refs.author).value = '';
        React.findDOMNode(this.refs.text).value = '';
    },
    render: function() {
        return (
            React.createElement("form", {className: "commentForm", onSubmit: this.handleSubmit},
                React.createElement("input", {type: "text", placeholder: "Your name", ref: "author"}),
                React.createElement("input", {type: "text", placeholder: "Say something...", ref: "text"}),
                React.createElement("input", {type: "submit", value: "Post"})
            )
        );
    }
});

React.render(
    React.createElement(CommentBox, {url: "http://127.0.0.1:8083/products?from=1998-11-11&to=2100-11-11", pollInterval: 200000}),
    document.getElementById('content')
);